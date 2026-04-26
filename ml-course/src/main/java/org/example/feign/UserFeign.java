package org.example.feign;

import org.example.entity.User;
import org.example.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 接口说明：远程调用用户微服务
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-26 16:53
 */
@FeignClient(name = "ml-user")
public interface UserFeign {

    /**
     * 根据用户ID查询用户信息
     *
     * @param id 用户ID
     * @return 包含用户信息的Result对象
     */
    @GetMapping("/api/v1/user/select/{id}")
    Result<User> selectById(@PathVariable("id") Long id);
}

