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
import org.example.entity.Coupons;
import org.example.service.CouponsService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 优惠卷表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "优惠卷表接口")
@RequestMapping("/api/v1/coupons")
public class CouponsController {

    @Autowired
    private CouponsService couponsService;

    /**
     * 添加优惠卷表。
     *
     * @param coupons 优惠卷表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存优惠卷表")
    public boolean save(@RequestBody @Parameter(description="优惠卷表")Coupons coupons) {
        return couponsService.save(coupons);
    }

    /**
     * 根据主键删除优惠卷表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键优惠卷表")
    public boolean remove(@PathVariable @Parameter(description="优惠卷表主键")Long id) {
        return couponsService.removeById(id);
    }

    /**
     * 根据主键更新优惠卷表。
     *
     * @param coupons 优惠卷表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新优惠卷表")
    public boolean update(@RequestBody @Parameter(description="优惠卷表主键")Coupons coupons) {
        return couponsService.updateById(coupons);
    }

    /**
     * 查询所有优惠卷表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有优惠卷表")
    public List<Coupons> list() {
        return couponsService.list();
    }

    /**
     * 根据优惠卷表主键获取详细信息。
     *
     * @param id 优惠卷表主键
     * @return 优惠卷表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取优惠卷表")
    public Coupons getInfo(@PathVariable Long id) {
        return couponsService.getById(id);
    }

    /**
     * 分页查询优惠卷表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询优惠卷表")
    public Page<Coupons> page(@Parameter(description="分页信息")Page<Coupons> page) {
        return couponsService.page(page);
    }

}
