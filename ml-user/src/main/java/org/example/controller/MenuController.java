package org.example.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.entity.Menu;
import org.example.service.MenuService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 菜单表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "菜单表接口")
@RequestMapping("/api/v1/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 添加菜单表。
     *
     * @param menu 菜单表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存菜单表")
    public boolean save(@RequestBody @Parameter(description="菜单表")Menu menu) {
        return menuService.save(menu);
    }

    /**
     * 根据主键删除菜单表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键菜单表")
    public boolean remove(@PathVariable @Parameter(description="菜单表主键")Long id) {
        return menuService.removeById(id);
    }

    /**
     * 根据主键更新菜单表。
     *
     * @param menu 菜单表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新菜单表")
    public boolean update(@RequestBody @Parameter(description="菜单表主键")Menu menu) {
        return menuService.updateById(menu);
    }

    /**
     * 查询所有菜单表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有菜单表")
    public List<Menu> list() {
        return menuService.list();
    }

    /**
     * 根据菜单表主键获取详细信息。
     *
     * @param id 菜单表主键
     * @return 菜单表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取菜单表")
    public Menu getInfo(@PathVariable Long id) {
        return menuService.getById(id);
    }

    /**
     * 分页查询菜单表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询菜单表")
    public Page<Menu> page(@Parameter(description="分页信息")Page<Menu> page) {
        return menuService.page(page);
    }

    @GetMapping("listMenusByRoleId/{roleId}")
    @Operation(description="查询角色菜单ID列表")
    public List<Long> listMenuidsByRoleId(@PathVariable("roleId") Long roleId) {
        return menuService.listMenuIdsByRoleId(roleId);
    }

    @PutMapping("updateMenusByRoleId")
    @Operation(description="修改角色菜单列表")
    public boolean updateMenusByRoleId(@RequestParam("roleId") Long roleId
                             ,@RequestParam("menuIds") List<Long> menuIds) {
        return menuService.updateMenuIdsRoleId(roleId, menuIds);
    }
}
