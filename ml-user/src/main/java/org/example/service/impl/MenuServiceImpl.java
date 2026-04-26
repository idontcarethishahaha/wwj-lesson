package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.dto.MenuInsertDTO;
import org.example.dto.MenuPageDTO;
import org.example.dto.MenuUpdateDTO;
import org.example.entity.Menu;
import org.example.entity.RoleMenu;
import org.example.exception.ServiceException;
import org.example.mapper.MenuMapper;
import org.example.mapper.RoleMenuMapper;
import org.example.result.ResultCode;
import org.example.service.MenuService;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.example.entity.table.MenuTableDef.MENU;
import static org.example.entity.table.RoleMenuTableDef.ROLE_MENU;

/**
 * 菜单表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>  implements MenuService{
    @Override
    public boolean save(MenuInsertDTO dto) {
        String title = dto.getTitle();
        boolean exists = QueryChain.of(mapper)
                .where(MENU.TITLE.eq(title))
                .exists();
        if (exists) {
            throw new ServiceException(ResultCode.TITLE_REPEAT,"该菜单已存在，请勿重复添加");
        }
        Menu menu = BeanUtil.copyProperties(dto, Menu.class);
        if(StrUtil.isBlank(dto.getInfo())){
            menu.setInfo("暂无介绍");
        }
        if(mapper.insert(menu) == 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR,"添加新菜单失败");
        }
        return true;
    }

    @Override
    public boolean update(MenuUpdateDTO dto) {
        return false;
    }

    @Override
    public Menu getById(Long id) {
        Menu menu = mapper.selectOneById(id);
        if(ObjectUtil.isNull(menu)){
            throw new ServiceException(ResultCode.MENU_NOT_FOUND,"未找到该菜单");
        }
        return menu;
    }

    @Override
    public PageVO<Menu> page(MenuPageDTO dto) {
        QueryChain<Menu> queryChain=QueryChain.of(mapper)
                .orderBy(MENU.PID.asc(),MENU.IDX.asc(),MENU.ID.desc());
        // 父菜单ID做条件
        if(ObjectUtil.isNotNull(dto.getPid())){
            queryChain.where(MENU.PID.eq(dto.getPid()));
        }
        // 菜单标题做条件
        if(StrUtil.isNotBlank(dto.getTitle())){
            queryChain.where(MENU.TITLE.like(dto.getTitle()));
        }
        // 分页查询转VO
        Page<Menu> result=queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Menu> pageVO=new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return QueryChain.of(roleMenuMapper)
                .select(ROLE_MENU.FK_MENU_ID)
                .where(ROLE_MENU.FK_ROLE_ID.eq(roleId))
                .objListAs(Long.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMenuIdsRoleId(Long roleId, List<Long> menuIds) {
        // 先移除原有的
        UpdateChain.of(roleMenuMapper)
                .where(ROLE_MENU.FK_ROLE_ID.eq(roleId))
                .remove();
        if(CollUtil.isEmpty(menuIds)){
            return true;
        }
        List<RoleMenu> roleMenus=new ArrayList<>();
        for(Long menuId:menuIds){
            RoleMenu roleMenu=new RoleMenu();
            roleMenu.setFkRoleId(roleId);
            roleMenu.setFkMenuId(menuId);
            roleMenus.add(roleMenu);
        }
        if(roleMenuMapper.insertBatch(roleMenus)< menuIds.size()){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"为角色批量分配菜单失败");
        }
        return true;
    }
}
