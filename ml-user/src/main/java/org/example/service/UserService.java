package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.UserPageDTO;
import org.example.entity.User;
import org.example.vo.PageVO;
import org.example.vo.UserSimpleListVO;

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
}
