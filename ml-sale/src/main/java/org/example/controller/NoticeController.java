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
import org.example.entity.Notice;
import org.example.service.NoticeService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @PostMapping("save")
    @Operation(description="保存通知表")
    public boolean save(@RequestBody @Parameter(description="通知表")Notice notice) {
        return noticeService.save(notice);
    }

    /**
     * 根据主键删除通知表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
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
    @GetMapping("getInfo/{id}")
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
    public Page<Notice> page(@Parameter(description="分页信息")Page<Notice> page) {
        return noticeService.page(page);
    }

}
