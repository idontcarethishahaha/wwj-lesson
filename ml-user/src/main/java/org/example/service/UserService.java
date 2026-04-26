package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.LoginByAccountDTO;
import org.example.dto.UserPageDTO;
import org.example.dto.UserUpdatePasswordDTO;
import org.example.entity.User;
import org.example.vo.LoginVO;
import org.example.vo.PageVO;
import org.example.vo.UserSimpleListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface UserService extends IService<User> {
     boolean save(User user);
     List<UserSimpleListVO> simpleList();
     PageVO<User> page(UserPageDTO dto);
     boolean update(User user);
     // 用户修改密码的方法
     boolean updatePassword(UserUpdatePasswordDTO dto);
     // 用户修改头像
     String updateAvatar(MultipartFile newFile,Long id);
     LoginVO loginByAccount(LoginByAccountDTO dto);
}
