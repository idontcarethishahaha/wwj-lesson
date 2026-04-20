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
import org.example.entity.Follow;
import org.example.service.FollowService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 收藏表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "收藏表接口")
@RequestMapping("/api/v1/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 添加收藏表。
     *
     * @param follow 收藏表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存收藏表")
    public boolean save(@RequestBody @Parameter(description="收藏表")Follow follow) {
        return followService.save(follow);
    }

    /**
     * 根据主键删除收藏表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键收藏表")
    public boolean remove(@PathVariable @Parameter(description="收藏表主键")Long id) {
        return followService.removeById(id);
    }

    /**
     * 根据主键更新收藏表。
     *
     * @param follow 收藏表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新收藏表")
    public boolean update(@RequestBody @Parameter(description="收藏表主键")Follow follow) {
        return followService.updateById(follow);
    }

    /**
     * 查询所有收藏表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有收藏表")
    public List<Follow> list() {
        return followService.list();
    }

    /**
     * 根据收藏表主键获取详细信息。
     *
     * @param id 收藏表主键
     * @return 收藏表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取收藏表")
    public Follow getInfo(@PathVariable Long id) {
        return followService.getById(id);
    }

    /**
     * 分页查询收藏表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询收藏表")
    public Page<Follow> page(@Parameter(description="分页信息")Page<Follow> page) {
        return followService.page(page);
    }

}
