package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.CategoryInsertDTO;
import org.example.dto.CategoryPageDTO;
import org.example.dto.CategoryUpdateDTO;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.example.vo.CategorySimpleListVO;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
     * @param dto 课程类别表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存课程类别表")
    public boolean save(@RequestBody @Parameter(description="课程类别表") CategoryInsertDTO dto) {
        return categoryService.insert(dto);
    }

    /**
     * 根据主键删除课程类别表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键课程类别表")
    public boolean remove(@PathVariable("id") @Parameter(description="课程类别表主键")Long id) {
        return categoryService.removeById(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除类别记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return categoryService.deleteBatch(ids);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条类别记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody CategoryUpdateDTO dto) {
        return categoryService.update(dto);
    }

    /**
     * 查询所有课程类别表。
     *
     * @return 所有数据
     */
    @GetMapping("simpleList")
    @Operation(description="查询所有课程类别表")
    public List<CategorySimpleListVO> list() {
        return categoryService.listSimple();
    }

    /**
     * 根据课程类别表主键获取详细信息。
     *
     * @param id 课程类别表主键
     * @return 课程类别表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取课程类别表")
    public Category getInfo(@PathVariable("id") Long id) {
        return categoryService.getById(id);
    }

    /**
     * 分页查询课程类别表。
     *
     * @param dto 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询课程类别表")
    public PageVO<Category> page(@Parameter(description="分页信息") CategoryPageDTO dto) {
        return categoryService.page(dto);
    }

}
