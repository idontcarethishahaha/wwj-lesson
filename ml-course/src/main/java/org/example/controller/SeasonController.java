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
import org.example.entity.Season;
import org.example.service.SeasonService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 季次表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "季次表接口")
@RequestMapping("/api/v1/season")
public class SeasonController {

    @Autowired
    private SeasonService seasonService;

    /**
     * 添加季次表。
     *
     * @param season 季次表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存季次表")
    public boolean save(@RequestBody @Parameter(description="季次表")Season season) {
        return seasonService.save(season);
    }

    /**
     * 根据主键删除季次表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键季次表")
    public boolean remove(@PathVariable @Parameter(description="季次表主键")Long id) {
        return seasonService.removeById(id);
    }

    /**
     * 根据主键更新季次表。
     *
     * @param season 季次表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新季次表")
    public boolean update(@RequestBody @Parameter(description="季次表主键")Season season) {
        return seasonService.updateById(season);
    }

    /**
     * 查询所有季次表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有季次表")
    public List<Season> list() {
        return seasonService.list();
    }

    /**
     * 根据季次表主键获取详细信息。
     *
     * @param id 季次表主键
     * @return 季次表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取季次表")
    public Season getInfo(@PathVariable Long id) {
        return seasonService.getById(id);
    }

    /**
     * 分页查询季次表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询季次表")
    public Page<Season> page(@Parameter(description="分页信息")Page<Season> page) {
        return seasonService.page(page);
    }

}
