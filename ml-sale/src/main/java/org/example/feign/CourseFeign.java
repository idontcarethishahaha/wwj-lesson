package org.example.feign;

/*
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-10 11:38
 */

import org.example.entity.Course;
import org.example.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "ml-course")
public interface CourseFeign {

    // 调用远程服务，获取课程信息
    @GetMapping("/api/v1/course/select/{id}")
    Result<Course> selectById(@PathVariable("id") Long id);
}
