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
import org.example.entity.Seckill;
import org.example.service.SeckillService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 秒杀表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "秒杀表接口")
@RequestMapping("/api/v1/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 添加秒杀表。
     *
     * @param seckill 秒杀表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存秒杀表")
    public boolean save(@RequestBody @Parameter(description="秒杀表")Seckill seckill) {
        return seckillService.save(seckill);
    }

    /**
     * 根据主键删除秒杀表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键秒杀表")
    public boolean remove(@PathVariable @Parameter(description="秒杀表主键")Long id) {
        return seckillService.removeById(id);
    }

    /**
     * 根据主键更新秒杀表。
     *
     * @param seckill 秒杀表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新秒杀表")
    public boolean update(@RequestBody @Parameter(description="秒杀表主键")Seckill seckill) {
        return seckillService.updateById(seckill);
    }

    /**
     * 查询所有秒杀表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有秒杀表")
    public List<Seckill> list() {
        return seckillService.list();
    }

    /**
     * 根据秒杀表主键获取详细信息。
     *
     * @param id 秒杀表主键
     * @return 秒杀表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取秒杀表")
    public Seckill getInfo(@PathVariable Long id) {
        return seckillService.getById(id);
    }

    /**
     * 分页查询秒杀表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询秒杀表")
    public Page<Seckill> page(@Parameter(description="分页信息")Page<Seckill> page) {
        return seckillService.page(page);
    }

}
