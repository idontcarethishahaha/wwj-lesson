package org.example.feign;

import io.swagger.v3.oas.annotations.Operation;
import org.example.entity.Course;
import org.example.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 接口说明：远程调用课程服务
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-14 19:02
 */
@FeignClient(name = "ml-course")
public interface CourseFeign {
    @GetMapping("/api/v1/course/select/{id}")
    @Operation(description = "根据主键获取课程表")
    Result<Course> getInfo(@PathVariable Long id);
}
