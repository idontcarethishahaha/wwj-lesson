package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.*;
import org.example.entity.User;
import org.example.vo.LoginVO;
import org.example.vo.PageVO;
import org.example.vo.UserSimpleListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 用户表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface UserService extends IService<User> {
     boolean save(UserInsertDTO dto);
     List<UserSimpleListVO> simpleList();
     PageVO<User> page(UserPageDTO dto);

     // 用户修改密码的方法
     boolean updatePassword(UserUpdatePasswordDTO dto);
     // 用户修改头像
     String updateAvatar(MultipartFile newFile,Long id);
     LoginVO loginByAccount(LoginByAccountDTO dto);

     /**
      * 重置用户密码
      *
      * @param id 用户主键
      * @return 用户的默认密码
      */
     String resetPassword(Long id);

     /**
      * 获取用户记录的Excel数据
      *
      * @return 用户记录的Excel数据列表
      */
     List<UserExcelDTO> getExcelData();

     boolean update(UserUpdateDTO dto);

     boolean delete(Long id);
     boolean deleteBatch(List<Long> ids);

     /**
      * 获取旧手机号码的解绑验证码
      *
      * @param id 用户主键
      * @return 一个随机的6位短信验证码
      */
     String getUnboundVcode(Long id);

     /**
      * 校验旧手机号码的解绑验证码
      *
      * @param id    用户主键
      * @param vcode 验证码
      * @return true表示验证码正确，false表示验证码错误
      */
     boolean checkUnboundVcode(Long id, String vcode);

     /**
      * 获取新手机号码的绑定验证码
      *
      * @param phone 新手机号码
      * @return 一个随机的6位短信验证码
      */
     String getBoundVcode(String phone);

     /**
      * 修改手机号码
      *
      * @param dto 用户修改手机号码DTO
      * @return true表示验证码正确，false表示验证码错误
      */
     boolean updatePhone(UserUpdatePhoneDTO dto);

     /**
      * 统计用户数据，包括用户性别比例，日增用户数量，用户总数等
      *
      * @return 统计结果
      */
     Map<String, Object> statistics();

     /**
      * 更新用户基本信息（昵称、签名等）
      *
      * @param userId 用户ID
      * @param nickname 昵称
      * @param signature 签名
      * @return 更新是否成功
      */
     boolean updateInfo(Long userId, String nickname, String signature);

}
