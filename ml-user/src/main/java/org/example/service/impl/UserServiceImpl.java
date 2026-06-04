package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.*;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.component.MyRedis;
import org.example.constant.ML;
import org.example.dto.*;
import org.example.entity.*;
import org.example.exception.ServiceException;
import org.example.mapper.RoleMenuMapper;
import org.example.mapper.UserMapper;
import org.example.mapper.UserRoleMapper;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mybatisflex.core.query.QueryMethods.*;
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

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    public boolean save(UserInsertDTO dto) {
        User user = BeanUtil.copyProperties(dto, User.class);
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
        user.setNickname(RandomUtil.randomString(10));
        user.setProvince(UserUtil.defaultProvince(idcard));//添加省份
        user.setGender(UserUtil.defaultGender(idcard));
        user.setAge(UserUtil.defaultAge(idcard));
        user.setZodiac(UserUtil.defaultZodiac(idcard));
        user.setAvatar(UserUtil.defaultAvatar(idcard));
        if(StrUtil.isBlank(user.getInfo())){
            user.setInfo("该用户很懒，没有留下任何描述");
        }
        // 对密码进行加密
        // Spring 自带的 BCrypt 加密算法,它是一个单向的hash算法，而且使用了自动生成的随机salt
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
        // 设置创建时间和修改时间
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
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

    @Override
    public String resetPassword(Long id) {

        // 检查用户是否存在
        //==================================
        this.existsById(id);

        // 重置密码
        // update user set password = ? where id = ?
        if (!UpdateChain.of(mapper)
                .set(USER.PASSWORD, BCrypt.hashpw(ML.User.DEFAULT_PASSWORD, BCrypt.gensalt(10)))
                .where(USER.ID.eq(id))
                .update()) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库重置密码失败");
        }

        // 返回用户的默认密码
        return ML.User.DEFAULT_PASSWORD;
    }

    /**
     * 判断用户是否存在，不存在则抛出异常
     * @param id 用户ID
     */
    private void existsById(Serializable id) {
        // 查询数据库判断用户是否存在
        boolean exists = QueryChain.of(mapper)
                .where(USER.ID.eq(id))
                .exists();

        // 不存在则抛出异常
        if (!exists) {
            throw new ServiceException(ResultCode.USER_NOT_FOUND, "该用户不存在");
        }
    }


    @Override
    public List<UserExcelDTO> getExcelData() {

        // 查询全部用户记录
        // select * from user
        List<User> users = QueryChain.of(mapper)
                .withRelations()
                .list();

        // 类型转换：List<User> -> List<UserExcelDTO>
        List<UserExcelDTO> result = new ArrayList<>();
        users.forEach(user -> {
            UserExcelDTO userExcelDTO = BeanUtil.copyProperties(user, UserExcelDTO.class);
            userExcelDTO.setGender(ML.User.genderFormat(user.getGender()));
            userExcelDTO.setRealname(DesensitizedUtil.chineseName(user.getRealname()));
            userExcelDTO.setIdcard(DesensitizedUtil.idCardNum(user.getIdcard(), 6, 3));
            userExcelDTO.setPhone(DesensitizedUtil.mobilePhone(user.getPhone()));
            result.add(userExcelDTO);
        });
        return result;
    }

    @Override
    public boolean update(UserUpdateDTO dto) {

        // 检查用户是否存在
        this.existsById(dto.getId());

        // 邮箱查重
        // select count(*) from user where email = ? and id != ?
        String email = dto.getEmail();
        if (StrUtil.isNotEmpty(email) && QueryChain.of(mapper)
                .where(USER.EMAIL.eq(dto.getEmail()))
                .and(USER.ID.ne(dto.getId()))
                .exists()) {
            throw new ServiceException(ResultCode.EMAIL_REPEAT, "电子邮箱" + email + "重复");
        }

        // 组装实体类
        User user = BeanUtil.copyProperties(dto, User.class);
        user.setUpdated(LocalDateTime.now());

        // update user set username=?, password=?, nickname=?, avatar=?, phone=?, email=?, gender=?, age=?, zodiac=?, province=?, realname=?, idcard=?, info=?, updated=? where id = ?
        if (!UpdateChain.of(user)
                .where(USER.ID.eq(user.getId()))
                .update()) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库修改失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(List<Long> ids) {

        // 检查用户是否存在
        // select count(*) from user where id in (?)
        if (QueryChain.of(mapper)
                .where(USER.ID.in(ids))
                .count() < ids.size()) {
            throw new ServiceException(ResultCode.USER_NOT_FOUND, "至少一个用户数据不存在");
        }

        // 删除中间表
        // delete from user_role where fk_user_id in (?)
        UpdateChain.of(userRoleMapper)
                .where(USER_ROLE.FK_USER_ID.in(ids))
                .remove();

        // 删除基本表
        // delete from user where id in (?)
        if (mapper.deleteBatchByIds(ids) != ids.size()) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Long id) {

        // 检查用户是否存在
        this.existsById(id);

        // 删除中间表
        // delete from user_role where fk_user_id = ?
        UpdateChain.of(userRoleMapper)
                .where(USER_ROLE.FK_USER_ID.eq(id))
                .remove();

        // 删除基本表
        // delete from user where id = ?
        if (mapper.deleteById(id) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
        return true;
    }
    //==========================换绑手机号码================================

    @Resource
    private MyRedis redis;

    @Override
    public String getUnboundVcode(Long id) {

        // 通过用户主键查询旧的手机号码
        // select phone from user where id = ?
        String phone = QueryChain.of(mapper)
                .select(USER.PHONE)
                .where(USER.ID.eq(id))
                .objAs(String.class);
        if (ObjectUtil.isNull(phone)) {
            throw new ServiceException(ResultCode.PHONE_NOT_FOUND, "手机号码" + phone + "不存在");
        }

        // 将短信验证码存入redis中，有效期5分钟
        String key = ML.Redis.UNBOUND_VCODE_PREFIX + phone;
        String val = RandomUtil.randomNumbers(6);
        redis.setEx(key, val, 5, TimeUnit.MINUTES);
        // todo: 向指定手机号码发送验证码

        // 将短信验证码返回给客户端
        return val;
    }

    @Override
    public boolean checkUnboundVcode(Long id, String vcode) {

        // 通过用户主键获取 phone 字段
        // select phone from user where id = ?
        String phone = QueryChain.of(mapper)
                .select(USER.PHONE)
                .where(USER.ID.eq(id))
                .objAs(String.class);
        if (ObjectUtil.isNull(phone)) {
            throw new ServiceException(ResultCode.PHONE_NOT_FOUND, "手机号码" + phone + "不存在");
        }

        // 校验验证码是否有效
        String key = ML.Redis.UNBOUND_VCODE_PREFIX + phone;
        String vcodeFromRedis = redis.get(key);
        if (ObjectUtil.isNull(vcodeFromRedis)) {
            throw new ServiceException(ResultCode.VCODE_ILLEGAL, "验证码" + vcode + "失效");
        }

        // 校验验证码是否正确，校验成功后，删除旧的验证码
        boolean result = vcodeFromRedis.equals(vcode);
        if (result) {
            redis.del(key);
        } else {
            throw new ServiceException(ResultCode.VCODE_ILLEGAL, "验证码" + vcode + "错误");
        }
        return true;
    }

    @Override
    public String getBoundVcode(String phone) {

        // 手机号码查重
        // select count(*) from user where phone = ?
        if (QueryChain.of(mapper)
                .select(USER.PHONE)
                .where(USER.PHONE.eq(phone))
                .exists()) {
            throw new ServiceException(ResultCode.PHONE_REPEAT, "手机号码" + phone + "重复");
        }

        // 将短信验证码存入redis中，有效期5分钟
        String key = ML.Redis.BOUND_VCODE_PREFIX + phone;
        String val = RandomUtil.randomNumbers(6);
        redis.setEx(key, val, 5, TimeUnit.MINUTES);
        // todo: 向指定手机号码发送验证码

        // 将短信验证码返回给客户端
        return val;
    }

    @Override
    public boolean updatePhone(UserUpdatePhoneDTO dto) {
        Long id = dto.getId();
        String phone = dto.getPhone();
        String vcode = dto.getVcode();

        // 检查用户是否存在
        this.existsById(id);

        // 手机号码查重
        // select count(*) from user where phone = ?
        if (QueryChain.of(mapper)
                .select(USER.PHONE)
                .where(USER.PHONE.eq(dto.getPhone()))
                .exists()) {
            throw new ServiceException(ResultCode.PHONE_REPEAT, "手机号码" + phone + "重复");
        }

        // 校验验证码是否有效
        String key = ML.Redis.BOUND_VCODE_PREFIX + phone;
        String vcodeFromRedis = redis.get(key);
        if (ObjectUtil.isNull(vcodeFromRedis)) {
            throw new ServiceException(ResultCode.VCODE_ILLEGAL, "验证码" + vcode + "失效");
        }

        // 校验验证码是否正确
        if (!vcodeFromRedis.equals(dto.getVcode())) {
            throw new ServiceException(ResultCode.VCODE_ILLEGAL, "验证码" + vcode + "错误");
        }

        // 修改用户手机号，修改成功后，删除旧的验证码
        // update user set phone = ? where id = ?
        if (UpdateChain.of(mapper)
                .set(USER.PHONE, phone)
                .where(USER.ID.eq(dto.getId()))
                .update()) {
            redis.del(key);
        } else {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库修改手机号码失败");
        }
        return true;
    }



    @Override
    public Map<String, Object> statistics() {

        // 尝试从缓存中获取统计数据，若存在则直接返回
        String dataFromRedis = redis.get(ML.Redis.USER_STATISTICS_DATA_KEY);
        if (ObjectUtil.isNotNull(dataFromRedis)) {
            return JSONUtil.parseObj(dataFromRedis);
        }

        Map<String, Object> result = new HashMap<>();

        // 统计用户性别比例
        // select gender as name, count(*) as value from `user` group by gender
        result.put("genderCount", QueryChain.of(mapper)
                .select(USER.GENDER.as("name"), QueryMethods.count().as("value"))
                .groupBy(USER.GENDER)
                .orderBy(USER.GENDER.asc())
                .listAs(Map.class));

        // 统计今日用户数
        // select count(*) from `user` where datediff(curdate(), date_format(created, '%Y-%m-%d')) = 0
        double todayCount = QueryChain.of(mapper)
                .where(dateDiff(currentDate(), dateFormat(USER.CREATED, "%Y-%m-%d")).eq(0))
                .count();

        // 统计昨日用户数
        // select count(*) from `user` where datediff(curdate(), date_format(created, '%Y-%m-%d')) = 1
        double yesterdayCount = QueryChain.of(mapper)
                .where(dateDiff(currentDate(), dateFormat(USER.CREATED, "%Y-%m-%d")).eq(1))
                .count();

        // 统计今年用户数
        // select count(*) from `user` where year(created) = year(current_date);
        double thisYearCount = QueryChain.of(mapper)
                .where(year(USER.CREATED).eq(year(currentDate())))
                .count();

        // 统计去年用户总数
        // select count(*) from `user` where year(created) - year(current_date) = -1;
        double lastYearCount = QueryChain.of(mapper)
                .where(year(USER.CREATED).subtract(year(currentDate())).eq(-1))
                .count();

        result.put("todayCount", todayCount);
        result.put("yesterdayCount", yesterdayCount);
        result.put("dayIncrease", this.increase(todayCount, yesterdayCount));
        result.put("thisYearCount", thisYearCount);
        result.put("lastYearCount", lastYearCount);
        result.put("yearIncrease", this.increase(thisYearCount, lastYearCount));

        // 加入Redis缓存，2 个小时后过期
        redis.setEx(ML.Redis.USER_STATISTICS_DATA_KEY, JSONUtil.toJsonStr(result), 2, TimeUnit.HOURS);
        return result;
    }

    @Override
    public boolean updateInfo(Long userId, String nickname, String signature) {
        // 参数校验
        if (userId == null) {
            throw new ServiceException(ResultCode.ILLEGAL_PARAM, "用户ID不能为空");
        }
        
        // 检查用户是否存在
        User user = mapper.selectOneById(userId);
        if (user == null) {
            throw new ServiceException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        
        // 更新用户信息
        boolean success = UpdateChain.of(mapper)
                .set(USER.NICKNAME, nickname)
                .set(USER.INFO, signature)
                .where(USER.ID.eq(userId))
                .update();
        
        if (!success) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "更新用户信息失败");
        }
        
        return true;
    }

    /**
     * 计算a到b的增长率
     *
     * @param a 第一个操作数
     * @param b 第二个操作数
     * @return 保留两位小数的增长率
     */
    private String increase(double a, double b) {
        if (b == 0) {
            return a > b ? "100.00" : a < b ? "-100.00" : "0";
        }
        return String.format("%.2f", (a - b) / b);
    }

    @Override
    public Map<String, Object> getRole(Long userId) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        // 参数校验
        if (userId == null) {
            throw new ServiceException(ResultCode.ILLEGAL_PARAM, "用户ID不能为空");
        }
        
        // 查询用户的角色
        List<Long> roleIds = QueryChain.of(userRoleMapper)
                .select(USER_ROLE.FK_ROLE_ID)
                .where(USER_ROLE.FK_USER_ID.eq(userId))
                .objListAs(Long.class);
        
        if (CollUtil.isEmpty(roleIds)) {
            result.put("role", "普通用户");
            return result;
        }
        
        // 查询角色名称
        List<String> roleNames = QueryChain.of(Role.class)
                .select(ROLE.TITLE)
                .where(ROLE.ID.in(roleIds))
                .objListAs(String.class);
        
        result.put("role", roleNames.isEmpty() ? "普通用户" : roleNames.get(0));
        
        return result;
    }

}
