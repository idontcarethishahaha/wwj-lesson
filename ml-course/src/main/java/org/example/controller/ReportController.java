package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.ReportInsertDTO;
import org.example.dto.ReportPageDTO;
import org.example.entity.Report;
import org.example.service.ReportService;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 举报表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "举报表接口")
@RequestMapping("/api/v1/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 添加举报表。
     *
     * @param report 举报表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存举报表")
    public boolean save(@RequestBody @Parameter(description="举报表") ReportInsertDTO report) {
        return reportService.insert(report);
    }

    /**
     * 根据主键删除举报表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键举报表")
    public boolean remove(@PathVariable("id") @Parameter(description="举报表主键")Long id) {
        return reportService.removeById(id);
    }

    /**
     * 根据主键更新举报表。
     *
     * @param report 举报表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新举报表")
    public boolean update(@RequestBody @Parameter(description="举报表主键")Report report) {
        return reportService.updateById(report);
    }

    /**
     * 查询所有举报表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有举报表")
    public List<Report> list() {
        return reportService.list();
    }

    /**
     * 根据举报表主键获取详细信息。
     *
     * @param id 举报表主键
     * @return 举报表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取举报表")
    public Report getInfo(@PathVariable("id") Long id) {
        return reportService.getById(id);
    }

    /**
     * 分页查询举报表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询举报表")
    public PageVO<Report> page(@Parameter(description="分页信息") ReportPageDTO page) {
        return reportService.page(page);
    }

}
