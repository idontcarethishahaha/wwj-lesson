package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.CourseInsertDTO;
import org.example.dto.CoursePageDTO;
import org.example.dto.CourseUpdateDTO;
import org.example.entity.Course;
import org.example.result.Result;
import org.example.service.CourseService;
import org.example.vo.CourseSimpleListVO;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条课程记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return courseService.delete(id);
    }
//    @DeleteMapping("delete/{id}")
//    @Operation(description="根据主键课程表")
//    public boolean remove(@PathVariable @Parameter(description="课程表主键")Long id) {
//        return courseService.removeById(id);
//    }


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
    public Course getInfo(@PathVariable("id") Long id) {
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
    public Result<String> uploadCover(@PathVariable("id") Long id, @RequestParam("coverFile") MultipartFile coverFile) {
        return new Result<>(courseService.uploadCover(id, coverFile));
    }

    // 仿照上传课程封面，创建一个上传课程摘要的方法
    @Operation(description="上传课程摘要")
    @PostMapping("uploadSummary/{id}")
    public Result<String> uploadSummary(@PathVariable("id") Long id, @RequestParam("summaryFile") MultipartFile summaryFile) {
        return new Result<>(courseService.uploadSummary(id, summaryFile));
    }


    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条课程记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody CourseUpdateDTO dto) {
        return courseService.update(dto);
    }

//
//    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除课程记录")
//    @DeleteMapping("deleteBatch")
//    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
//        return courseService.deleteBatch(ids);
//    }


}
