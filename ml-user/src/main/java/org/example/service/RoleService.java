package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.RoleInsertDTO;
import org.example.dto.RolePageDTO;
import org.example.entity.Role;
import org.example.vo.PageVO;
import org.example.vo.RoleSimpleListVO;

import java.util.List;

/**
 * 角色表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface RoleService extends IService<Role> {
      boolean save(RoleInsertDTO dto);
      Role getById(Long id);
      List<RoleSimpleListVO> simpleList();
      PageVO<Role> page(RolePageDTO vo);
      // 查询用户角色的方法
      List<Long> listRoleIdsByUserId(Long userId);
      // 修改用户角色的方法
      boolean updateRolesByUserId(Long userId, List<Long> roleIds);

}
