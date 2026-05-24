package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.CommentInsertDTO;
import org.example.dto.CommentPageDTO;
import org.example.entity.Comment;
import org.example.service.CommentService;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "评论表接口")
@RequestMapping("/api/v1/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论表。
     *
     * @param comment 评论表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("insert")
    @Operation(description="保存评论表")
    public boolean save(@RequestBody @Parameter(description="评论表") CommentInsertDTO comment) {
        return commentService.insert(comment);
    }

    /**
     * 根据主键删除评论表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键评论表")
    public boolean remove(@PathVariable("id") @Parameter(description="评论表主键")Long id) {
        return commentService.removeById(id);
    }

    /**
     * 根据主键更新评论表。
     *
     * @param comment 评论表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新评论表")
    public boolean update(@RequestBody @Parameter(description="评论表主键")Comment comment) {
        return commentService.updateById(comment);
    }

    /**
     * 查询所有评论表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有评论表")
    public List<Comment> list() {
        return commentService.list();
    }

    /**
     * 根据评论表主键获取详细信息。
     *
     * @param id 评论表主键
     * @return 评论表详情
     */
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取评论表")
    public Comment getInfo(@PathVariable("id") Long id) {
        return commentService.getById(id);
    }

    /**
     * 分页查询评论表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询评论表")
    public PageVO<Comment> page(@Parameter(description="分页信息") CommentPageDTO page) {
        return commentService.page(page);
    }

}
