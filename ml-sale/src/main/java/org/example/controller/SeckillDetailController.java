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
import org.example.entity.SeckillDetail;
import org.example.service.SeckillDetailService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 秒杀明细表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "秒杀明细表接口")
@RequestMapping("/api/v1/seckillDetail")
public class SeckillDetailController {

    @Autowired
    private SeckillDetailService seckillDetailService;

    /**
     * 添加秒杀明细表。
     *
     * @param seckillDetail 秒杀明细表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存秒杀明细表")
    public boolean save(@RequestBody @Parameter(description="秒杀明细表")SeckillDetail seckillDetail) {
        return seckillDetailService.save(seckillDetail);
    }

    /**
     * 根据主键删除秒杀明细表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键秒杀明细表")
    public boolean remove(@PathVariable @Parameter(description="秒杀明细表主键")Long id) {
        return seckillDetailService.removeById(id);
    }

    /**
     * 根据主键更新秒杀明细表。
     *
     * @param seckillDetail 秒杀明细表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新秒杀明细表")
    public boolean update(@RequestBody @Parameter(description="秒杀明细表主键")SeckillDetail seckillDetail) {
        return seckillDetailService.updateById(seckillDetail);
    }

    /**
     * 查询所有秒杀明细表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有秒杀明细表")
    public List<SeckillDetail> list() {
        return seckillDetailService.list();
    }

    /**
     * 根据秒杀明细表主键获取详细信息。
     *
     * @param id 秒杀明细表主键
     * @return 秒杀明细表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取秒杀明细表")
    public SeckillDetail getInfo(@PathVariable Long id) {
        return seckillDetailService.getById(id);
    }

    /**
     * 分页查询秒杀明细表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询秒杀明细表")
    public Page<SeckillDetail> page(@Parameter(description="分页信息")Page<SeckillDetail> page) {
        return seckillDetailService.page(page);
    }

}
