package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.*;
import org.example.entity.User;
import org.example.result.Result;
import org.example.service.UserService;
import org.example.util.ExcelUtil;
import org.example.vo.LoginVO;
import org.example.vo.PageVO;
import org.example.vo.UserSimpleListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
     * @param dto 用户表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存用户表")
    public boolean save(@RequestBody @Parameter(description="用户表") UserInsertDTO dto) {
        return userService.save(dto);
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
    public User getInfo(@PathVariable("id") Long id) {
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

    @Operation(summary = "修改 - 重置密码", description = "按主键重置用户的登录密码为默认密码，重置成功后返回默认密码")
    @PutMapping("resetPassword/{id}")
    public Result<String> resetPassword(@PathVariable("id") Long id) {
        return new Result<>(userService.resetPassword(id));
    }


    @Operation(summary = "用户 - 修改头像", description = "用户修改自己的头像")
    @PutMapping("updateAvatar/{id}")
    public Result<String> updateAvatar(@PathVariable("id") Long id,
                                       @RequestParam("avatarFile") MultipartFile avatar){
        return new Result<>(userService.updateAvatar(avatar,id));
    }

    @Operation(summary = "用户 - 登录", description = "用户登录")
    @PostMapping("loginByAccount")
    public LoginVO loginByAccount(@Validated @RequestBody LoginByAccountDTO dto){
        return userService.loginByAccount(dto);
    }

    @Operation(summary = "查询 - 报表打印", description = "打印用户相关的报表数据")
    @GetMapping("excel")
    public void excel(HttpServletResponse response) {
        ExcelUtil.download(response, "用户统计表", userService.getExcelData());
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条用户记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody UserUpdateDTO dto) {
        return userService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条用户记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除用户记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return userService.deleteBatch(ids);
    }
    //=================换绑手机号码=======================
    @Operation(summary = "查询 - 解绑验证码", description = "获取旧手机号码的解绑验证码")
    @GetMapping("getUnboundVcode/{id}")
    public Result<String> getUnboundVcode(@PathVariable("id") Long id) {
        return new Result<>(userService.getUnboundVcode(id));
    }

    @Operation(summary = "校验 - 解绑验证码", description = "校验旧手机号码的解绑验证码")
    @GetMapping("checkUnboundVcode/{id}/{vcode}")
    public boolean checkUnboundVcode(@PathVariable("id") Long id,
                                     @PathVariable("vcode") String vcode) {
        return userService.checkUnboundVcode(id, vcode);
    }

    @Operation(summary = "查询 - 绑定验证码", description = "获取新手机号码的绑定验证码")
    @GetMapping("getBoundVcode/{phone}")
    public Result<String> getBoundVcode(@PathVariable("phone") String phone) {
        return new Result<>(userService.getBoundVcode(phone));
    }

    @Operation(summary = "修改 - 手机号码", description = "修改用户的手机号码")
    @PutMapping("updatePhone")
    public boolean updatePhone(@Validated @RequestBody UserUpdatePhoneDTO dto) {
        return userService.updatePhone(dto);
    }

    @Operation(summary = "查询 - 统计数据", description = "查询用户相关的统计数据")
    @GetMapping("statistics")
    public Map<String, Object> statistics() {
        return userService.statistics();
    }

    @Operation(summary = "用户 - 更新基本信息", description = "用户更新自己的昵称和签名")
    @PutMapping("info")
    public boolean updateInfo(@RequestBody Map<String, Object> data) {
        System.out.println("收到更新请求，数据: " + data);
        Long userId = data.get("userId") != null ? ((Number) data.get("userId")).longValue() : null;
        System.out.println("解析后的userId: " + userId);
        String nickname = (String) data.get("nickname");
        String signature = (String) data.get("signature");
        return userService.updateInfo(userId, nickname, signature);
    }

    @Operation(summary = "用户 - 获取角色信息", description = "获取当前登录用户的角色和权限")
    @GetMapping("role")
    public Map<String, Object> getRole(@RequestParam(required = false) Long userId) {
        // 如果没有传递 userId，默认使用用户ID为1（实际项目中应该从JWT token中解析）
        if (userId == null) {
            userId = 1L;
        }
        System.out.println("=== getRole 接口被调用 ===");
        System.out.println("userId: " + userId);
        Map<String, Object> result = userService.getRole(userId);
        System.out.println("返回结果: " + result);
        return result;
    }


}
