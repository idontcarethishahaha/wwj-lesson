package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.RolePageDTO;
import org.example.entity.Role;
import org.example.service.RoleService;
import org.example.vo.PageVO;
import org.example.vo.RoleSimpleListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "角色表接口")
@RequestMapping("/api/v1/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 添加角色表。
     *
     * @param role 角色表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存角色表")
    public boolean save(@RequestBody @Parameter(description="角色表")Role role) {
        return roleService.save(role);
    }

    /**
     * 根据主键删除角色表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键角色表")
    public boolean remove(@PathVariable @Parameter(description="角色表主键")Long id) {
        return roleService.removeById(id);
    }

    /**
     * 根据主键更新角色表。
     *
     * @param role 角色表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新角色表")
    public boolean update(@RequestBody @Parameter(description="角色表主键")Role role) {
        return roleService.updateById(role);
    }

    /**
     * 查询所有角色表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有角色表")
    public List<Role> list() {
        return roleService.list();
    }

    /**
     * 根据角色表主键获取详细信息。
     *
     * @param id 角色表主键
     * @return 角色表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取角色表")
    public Role getInfo(@PathVariable Long id) {
        return roleService.getById(id);
    }

    /**
     * 分页查询角色表。
     *
     * @param dto 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询角色表")
    public PageVO<Role> page(@Parameter(description="分页信息")RolePageDTO dto) {
        return roleService.page(dto);
    }

    @GetMapping("simpleList")
    @Operation(description = "查询简单列表")
    public List<RoleSimpleListVO> simpleList() {
        return roleService.simpleList();//query作为查询条件
    }

    @GetMapping("listRoleIdsByUserId/{userId}")
    @Operation(description = "根据用户ID查询角色ID列表")
    public List<Long> listRoleIdsByUserId(@PathVariable Long userId) {
        return roleService.listRoleIdsByUserId(userId);
    }

    @PutMapping("updateRolesByUserId")
    @Operation(description = "修改用户角色")
    public boolean updateRolesByUserId(@RequestParam("userId") Long userId,
                                       @RequestParam("roleIds") List<Long> roleIds) {
        return roleService.updateRolesByUserId(userId, roleIds);
    }
}
