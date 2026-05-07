package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.CourseInsertDTO;
import org.example.dto.CoursePageDTO;
import org.example.entity.Course;
import org.example.result.Result;
import org.example.service.CourseService;
import org.example.vo.CourseSimpleListVO;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "课程表接口")
@RequestMapping("/api/v1/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    /**
     * 添加课程表。
     *
     * @param course 课程表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存课程表")
    public boolean save(@RequestBody @Parameter(description="课程表") CourseInsertDTO course) {
        return courseService.insert(course);
    }

    /**
     * 根据主键删除课程表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键课程表")
    public boolean remove(@PathVariable @Parameter(description="课程表主键")Long id) {
        return courseService.removeById(id);
    }

    /**
     * 根据主键更新课程表。
     *
     * @param course 课程表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新课程表")
    public boolean update(@RequestBody @Parameter(description="课程表主键")Course course) {
        return courseService.updateById(course);
    }

    /**
     * 查询所有课程表。
     *
     * @return 所有数据
     */
    @GetMapping("simpleList")
    @Operation(description="查询所有课程表")
    public List<CourseSimpleListVO> list() {
        return courseService.simpleList();
    }

    /**
     * 根据课程表主键获取详细信息。
     *
     * @param id 课程表主键
     * @return 课程表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取课程表")
    public Course getInfo(@PathVariable Long id) {
        return courseService.getById(id);
    }

    /**
     * 分页查询课程表。
     *
     * @param dto 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询课程表")
    public PageVO<Course> page(@Parameter(description="分页信息") CoursePageDTO dto) {
        return courseService.page(dto);
    }

    @Operation(description="上传课程封面")
    @PostMapping("uploadCover/{id}")
    public Result<String> uploadCover(@PathVariable Long id, @RequestParam("coverFile") MultipartFile coverFile) {
        return new Result<>(courseService.uploadCover(id, coverFile));
    }

    // 仿照上传课程封面，创建一个上传课程摘要的方法

}
