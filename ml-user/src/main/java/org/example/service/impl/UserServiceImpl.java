package org.example.service.impl;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.dto.UserPageDTO;
import org.example.entity.User;
import org.example.exception.ServiceException;
import org.example.mapper.UserMapper;
import org.example.result.ResultCode;
import org.example.service.UserService;
import org.example.util.UserUtil;
import org.example.vo.PageVO;
import org.example.vo.UserSimpleListVO;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<UserSimpleListVO> simpleList() {
        return List.of();
    }

    @Override
    public PageVO<User> page(UserPageDTO dto) {
        return null;
    }

    @Override
    public boolean update(User user) {
        return false;
    }
}
