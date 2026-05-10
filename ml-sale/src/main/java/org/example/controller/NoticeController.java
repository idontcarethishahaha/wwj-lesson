package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.NoticeInsertDTO;
import org.example.dto.NoticePageDTO;
import org.example.entity.Notice;
import org.example.service.NoticeService;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "通知表接口")
@RequestMapping("/api/v1/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 添加通知表。
     *
     * @param notice 通知表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存通知表")
    public boolean save(@RequestBody @Parameter(description="通知表") NoticeInsertDTO notice) {
        return noticeService.insert(notice);
    }

    /**
     * 根据主键删除通知表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键通知表")
    public boolean remove(@PathVariable @Parameter(description="通知表主键")Long id) {
        return noticeService.removeById(id);
    }

    /**
     * 根据主键更新通知表。
     *
     * @param notice 通知表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新通知表")
    public boolean update(@RequestBody @Parameter(description="通知表主键")Notice notice) {
        return noticeService.updateById(notice);
    }

    /**
     * 查询所有通知表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有通知表")
    public List<Notice> list() {
        return noticeService.list();
    }

    /**
     * 根据通知表主键获取详细信息。
     *
     * @param id 通知表主键
     * @return 通知表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取通知表")
    public Notice getInfo(@PathVariable Long id) {
        return noticeService.getById(id);
    }

    /**
     * 分页查询通知表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询通知表")
    public PageVO<Notice> page(@Parameter(description="分页信息") NoticePageDTO page) {
        return noticeService.page(page);
    }

    /**
     * 获取获取最新通知
     *
     * @return 最新通知
     */
    @GetMapping("top/{n}")
    @Operation(description="获取最新n个通知")
    public List<Notice> top(@PathVariable("n") @Parameter(description="获取最新n个通知") Long n) {
        return noticeService.top(n);
    }
}
