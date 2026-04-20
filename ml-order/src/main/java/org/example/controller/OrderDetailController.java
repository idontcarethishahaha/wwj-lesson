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
import org.example.entity.OrderDetail;
import org.example.service.OrderDetailService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 订单明细表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "订单明细表接口")
@RequestMapping("/api/v1/orderDetail")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 添加订单明细表。
     *
     * @param orderDetail 订单明细表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存订单明细表")
    public boolean save(@RequestBody @Parameter(description="订单明细表")OrderDetail orderDetail) {
        return orderDetailService.save(orderDetail);
    }

    /**
     * 根据主键删除订单明细表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键订单明细表")
    public boolean remove(@PathVariable @Parameter(description="订单明细表主键")Long id) {
        return orderDetailService.removeById(id);
    }

    /**
     * 根据主键更新订单明细表。
     *
     * @param orderDetail 订单明细表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新订单明细表")
    public boolean update(@RequestBody @Parameter(description="订单明细表主键")OrderDetail orderDetail) {
        return orderDetailService.updateById(orderDetail);
    }

    /**
     * 查询所有订单明细表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有订单明细表")
    public List<OrderDetail> list() {
        return orderDetailService.list();
    }

    /**
     * 根据订单明细表主键获取详细信息。
     *
     * @param id 订单明细表主键
     * @return 订单明细表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取订单明细表")
    public OrderDetail getInfo(@PathVariable Long id) {
        return orderDetailService.getById(id);
    }

    /**
     * 分页查询订单明细表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询订单明细表")
    public Page<OrderDetail> page(@Parameter(description="分页信息")Page<OrderDetail> page) {
        return orderDetailService.page(page);
    }

}
