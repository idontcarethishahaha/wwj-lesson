package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.component.MyRedis;
import org.example.constant.ML;
import org.example.dto.LoginByAccountDTO;
import org.example.dto.UserPageDTO;
import org.example.dto.UserUpdatePasswordDTO;
import org.example.entity.*;
import org.example.exception.ServiceException;
import org.example.mapper.UserMapper;
import org.example.result.ResultCode;
import org.example.service.UserService;
import org.example.util.MinioUtil;
import org.example.util.UserUtil;
import org.example.vo.LoginVO;
import org.example.vo.PageVO;
import org.example.vo.UserSimpleListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.entity.table.MenuTableDef.MENU;
import static org.example.entity.table.RoleMenuTableDef.ROLE_MENU;
import static org.example.entity.table.RoleTableDef.ROLE;
import static org.example.entity.table.UserRoleTableDef.USER_ROLE;
import static org.example.entity.table.UserTableDef.USER;

/**
 * 用户表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{
   //ctrl+i

    @Override
    public boolean save(User user) {
        String idcard=user.getIdcard();//获取用户的身份证号码
        if(!IdcardUtil.isValidCard(idcard)){
            throw new ServiceException(ResultCode.ID_CARD_ILLEGAL,"身份证号"+idcard+"有误");
        }
        String username=user.getUsername();
        if(QueryChain.of(mapper)
                .where(USER.USERNAME.eq(username))
                .exists()){
            throw new ServiceException(ResultCode.USERNAME_REPEAT,"用户名"+username+"已存在");
        }
        if(QueryChain.of(mapper)
                .where(USER.IDCARD.eq(idcard))
                .exists()){
            throw new ServiceException(ResultCode.ID_CARD_REPEAT,"身份证号"+idcard+"已存在");
        }
        if(QueryChain.of(mapper).where(USER.PHONE.eq(user.getPhone())).exists()){
            throw new ServiceException(ResultCode.PHONE_REPEAT,"手机号码"+user.getPhone()+"已存在");
        }
        if(QueryChain.of(mapper).where(USER.EMAIL.eq(user.getEmail())).exists()){
            throw new ServiceException(ResultCode.EMAIL_REPEAT,"邮箱"+user.getEmail()+"已存在");
        }
        user.setGender(UserUtil.defaultGender(idcard));
        user.setAge(UserUtil.defaultAge(idcard));
        user.setZodiac(UserUtil.defaultZodiac(idcard));
        user.setAvatar(UserUtil.defaultAvatar(idcard));
        if(StrUtil.isNotBlank(user.getInfo())){
            user.setInfo("该用户很懒，没有留下任何描述");
        }
        // 对密码进行加密
        // Spring 自带的 BCrypt 加密算法,它是一个单向的hash算法，而且使用了自动生成的随机salt
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
        if(mapper.insert(user)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"数据库添加失败");
        }
        return true;
    }

    @Override
    public User getById(Serializable id) {
        User user=mapper.selectOneById(id);
        if(ObjectUtil.isNull(user)){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"该用户不存在");
        }
        // 对用户的敏感信息脱敏：身份证、手机号码、真实姓名、密码置空
        return UserUtil.desensitization(user);
    }

    @Override
    public List<UserSimpleListVO> simpleList() {
        return QueryChain.of(mapper)
                .listAs(UserSimpleListVO.class);
    }

    @Override
    public PageVO<User> page(UserPageDTO dto) {
        QueryChain<User> queryChain = QueryChain.of(mapper);
        String username = dto.getUsername();
        if(ObjectUtil.isNotEmpty(username)){
            queryChain.where(USER.USERNAME.like(username));//基于用户名的模糊查询
        }
        String nickname = dto.getNickname();
        if(ObjectUtil.isNotEmpty(nickname)){
            queryChain.where(USER.NICKNAME.like(nickname));
        }
        String phone = dto.getPhone();
        if(ObjectUtil.isNotEmpty(phone)){
            queryChain.where(USER.PHONE.like(phone));
        }
        Page<User> result = queryChain.page(new Page<>(dto.getPageNum(), dto.getPageSize()));
        // 对查询到的列表进行脱敏操作
        result.setRecords(UserUtil.desensitization(result.getRecords()));

        PageVO<User> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result, pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

    // 自己写
    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public boolean updatePassword(UserUpdatePasswordDTO dto) {
        User user=mapper.selectOneById(dto.getId());
        if(ObjectUtil.isNull(user)){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"该用户不存在");
        }
        // 判断旧密码是否正确
        if(!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())){
            throw new ServiceException(ResultCode.OLD_PASSWORD_ILLEGAL,"旧密码错误");
        }
        // 将新密码更新到数据库
        boolean res= UpdateChain.of(mapper)
                .set(USER.PASSWORD,BCrypt.hashpw(dto.getNewPassword(),BCrypt.gensalt(10)))
                .where(USER.ID.eq(dto.getId()))
                .update();
        if(!res){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"修改密码失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateAvatar(MultipartFile newFile, Long id) {
        User user=mapper.selectOneById(id);
        // 判断用户是否存在
        if(ObjectUtil.isNull(user)){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"该用户不存在");
        }
        // 先将旧的文件名备份
        String oldFileName = user.getAvatar();
        // 随机生成新的文件名
//        String newFileName = MinioUtil.randomFilename(newFile);
        String newFileName = null;
        try{
            newFileName=MinioUtil.upload(newFile,ML.MinIO.AVATAR_DIR,ML.MinIO.BUCKET_NAME);
        }catch (Exception ex){
            throw new ServiceException(ResultCode.SERVER_ERROR,"MinIO操作失败：用户头像上传未成功");
        }
        user.setAvatar(newFileName);
        if(mapper.update(user)<=0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"更新数据库用户头像失败");
        }
        return newFileName;
    }
    @Autowired
    private MyRedis myRedis;//注入一个操作Redis缓存的对象

    @Override
    public LoginVO loginByAccount(LoginByAccountDTO dto) {
        // 先验证账号和密码是否正确
        String username = dto.getUsername();
        String password = dto.getPassword();
        User user=QueryChain.of(mapper)
                .where(USER.USERNAME.eq(username))
                .one();
        if(user==null || !BCrypt.checkpw(password,user.getPassword())){
            throw new ServiceException(ResultCode.ACCOUNT_ILLEGAL,"账号密码错误");
        }
        return buildLoginVO(user);
    }

    private LoginVO buildLoginVO(User user){
        LoginVO result=new LoginVO();
        // 生成令牌
        String tokenKey= UUID.randomUUID().toString();
        // 生成Token令牌，存入Redis,30分钟之后过期
        myRedis.setEx(tokenKey, JSONUtil.toJsonStr(user),30, TimeUnit.MINUTES);
        // 查询用户的角色信息
        List<Long> roleIds=QueryChain.of(UserRole.class)
                .select(USER_ROLE.FK_ROLE_ID)
                .where(USER_ROLE.FK_USER_ID.eq(user.getId()))
                .objListAs(Long.class);
        if(CollUtil.isEmpty(roleIds)){
            result.setRoleTitles(null);
            result.setMenus(null);
            result.setUser(UserUtil.desensitization(user));//用户信息脱敏
            result.setToken(tokenKey);
            return result;
        }
        // 使用角色id查找角色
        List<String> roleTitles=QueryChain.of(Role.class)
                .select(ROLE.TITLE)
                .where(ROLE.ID.in(roleIds))
                .objListAs(String.class);
        result.setRoleTitles(roleTitles);
        // 使用角色id查找菜单
        List<Long> menuIds=QueryChain.of(RoleMenu.class)
                .select(ROLE_MENU.FK_MENU_ID)
                .where(ROLE_MENU.FK_ROLE_ID.in(roleIds))
                .objListAs(Long.class);
        // 如果未查到菜单数据，直接返回
        if(CollUtil.isEmpty(menuIds)){
            result.setMenus(null);
            result.setRoleTitles(null);
            result.setUser(UserUtil.desensitization(user));//用户信息脱敏
            result.setToken(tokenKey);
            return result;
        }
        // 查询菜单数据
        RelationManager.addIgnoreRelations("parentMenu");
        List<Menu> menus=QueryChain.of(Menu.class)
                .where(MENU.ID.in(menuIds))
                .and(MENU.PID.eq(ML.Menu.ROOT_ID))
                .orderBy(MENU.IDX.asc(),MENU.ID.desc())
                .withRelations()
                .list();
        result.setMenus(menus);
        result.setUser(UserUtil.desensitization(user));
        result.setToken(tokenKey);
        return result;
    }
}
