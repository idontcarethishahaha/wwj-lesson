package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.MenuInsertDTO;
import org.example.dto.MenuPageDTO;
import org.example.dto.MenuUpdateDTO;
import org.example.entity.Menu;
import org.example.vo.PageVO;

import java.util.List;

/**
 * 菜单表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface MenuService extends IService<Menu> {
    boolean save(MenuInsertDTO dto);
    boolean update(MenuUpdateDTO dto);
    Menu getById(Long id);
    PageVO<Menu> page(MenuPageDTO dto);
    // 查询角色菜单
    List<Long> listMenuIdsByRoleId(Long roleId);
    // 给角色分配新的菜单
    boolean updateMenuIdsRoleId(Long roleId, List<Long> menuIds);
}
