package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.KillDTO;
import org.example.dto.SeckillInsertDTO;
import org.example.dto.SeckillPageDTO;
import org.example.entity.Seckill;
import org.example.result.Result;
import org.example.service.SeckillService;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("insert")
    @Operation(description="保存秒杀表")
    public boolean save(@RequestBody @Parameter(description="秒杀表") SeckillInsertDTO seckill) {
        return seckillService.save(seckill);
    }

    /**
     * 根据主键删除秒杀表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键秒杀表")
    public boolean remove(@PathVariable("id") Long id) {
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
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取秒杀表")
    public Seckill getInfo(@PathVariable("id") Long id) {
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
    public PageVO<Seckill> page(@Parameter(description="分页信息") SeckillPageDTO page) {
        return seckillService.page(page);
    }

    // 查询今日秒杀活动
    @GetMapping("today")
    @Operation(description="查询今日秒杀活动")
    public List<Seckill> queryTodaySeckill() {
        return seckillService.queryTodaySeckill();
    }

    @PostMapping("order")
    @Operation(description="秒杀下单")
    public Result order(@RequestBody @Parameter(description="秒杀下单参数") KillDTO dto){
        return new Result<>(seckillService.kill(dto));
    }

}
