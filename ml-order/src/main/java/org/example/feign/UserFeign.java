package org.example.feign;

import io.swagger.v3.oas.annotations.Operation;
import org.example.entity.User;
import org.example.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 接口说明：远程调用用户服务
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-14 19:02
 */
@FeignClient(name = "ml-user")
public interface UserFeign {
    @GetMapping("/api/v1/user/select/{id}")
    @Operation(description = "根据主键获取用户表")
    Result<User> getInfo(@PathVariable Long id);
}
