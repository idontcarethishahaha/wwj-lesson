package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.UserPageDTO;
import org.example.dto.UserUpdatePasswordDTO;
import org.example.entity.User;
import org.example.result.Result;
import org.example.service.UserService;
import org.example.vo.PageVO;
import org.example.vo.UserSimpleListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "用户表接口")
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 添加用户表。
     *
     * @param user 用户表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存用户表")
    public boolean save(@RequestBody @Parameter(description="用户表")User user) {
        return userService.save(user);
    }

    /**
     * 根据主键删除用户表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键用户表")
    public boolean remove(@PathVariable @Parameter(description="用户表主键")Long id) {
        return userService.removeById(id);
    }

    /**
     * 根据主键更新用户表。
     *
     * @param user 用户表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新用户表")
    public boolean update(@RequestBody @Parameter(description="用户表主键")User user) {
        return userService.updateById(user);
    }

    /**
     * 查询所有用户表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有用户表")
    public List<User> list() {
        return userService.list();
    }

    /**
     * 根据用户表主键获取详细信息。
     *
     * @param id 用户表主键
     * @return 用户表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取用户表")
    public User getInfo(@PathVariable Long id) {
        return userService.getById(id);
    }



    @GetMapping("simpleList")
    @Operation(description="查询简单列表：id,username,nickname")
    public List<UserSimpleListVO> simpleList() {
        return userService.simpleList();//query作为查询条件
    }

    /**
     * 分页查询用户表。
     *
     * @param dto 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询用户表")
    public PageVO<User> page(@Parameter(description="分页信息") UserPageDTO dto) {
        return userService.page(dto);
    }

    @Operation(summary = "用户 - 修改密码", description = "用户修改自己的密码")
    @PutMapping("updatePassword")
    public boolean updatePassword(@Validated @RequestBody UserUpdatePasswordDTO dto){
        return userService.updatePassword(dto);
    }

    @Operation(summary = "用户 - 修改头像", description = "用户修改自己的头像")
    @PutMapping("updateAvatar/{id}")
    public Result<String> updateAvatar(@PathVariable("id") Long id,
                                       @RequestParam("avatarFile") MultipartFile avatar){
        return new Result<>(userService.updateAvatar(avatar,id));
    }
}
