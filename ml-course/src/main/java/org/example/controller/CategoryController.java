package org.example.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 课程类别表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "课程类别表接口")
@RequestMapping("/api/v1/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加课程类别表。
     *
     * @param category 课程类别表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存课程类别表")
    public boolean save(@RequestBody @Parameter(description="课程类别表")Category category) {
        return categoryService.save(category);
    }

    /**
     * 根据主键删除课程类别表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键课程类别表")
    public boolean remove(@PathVariable @Parameter(description="课程类别表主键")Long id) {
        return categoryService.removeById(id);
    }

    /**
     * 根据主键更新课程类别表。
     *
     * @param category 课程类别表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新课程类别表")
    public boolean update(@RequestBody @Parameter(description="课程类别表主键")Category category) {
        return categoryService.updateById(category);
    }

    /**
     * 查询所有课程类别表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有课程类别表")
    public List<Category> list() {
        return categoryService.list();
    }

    /**
     * 根据课程类别表主键获取详细信息。
     *
     * @param id 课程类别表主键
     * @return 课程类别表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取课程类别表")
    public Category getInfo(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    /**
     * 分页查询课程类别表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询课程类别表")
    public Page<Category> page(@Parameter(description="分页信息")Page<Category> page) {
        return categoryService.page(page);
    }

}
