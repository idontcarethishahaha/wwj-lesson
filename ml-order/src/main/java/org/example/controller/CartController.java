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
import org.example.entity.Cart;
import org.example.service.CartService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 购物车表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "购物车表接口")
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车表。
     *
     * @param cart 购物车表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存购物车表")
    public boolean save(@RequestBody @Parameter(description="购物车表")Cart cart) {
        return cartService.save(cart);
    }

    /**
     * 根据主键删除购物车表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键购物车表")
    public boolean remove(@PathVariable @Parameter(description="购物车表主键")Long id) {
        return cartService.removeById(id);
    }

    /**
     * 根据主键更新购物车表。
     *
     * @param cart 购物车表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新购物车表")
    public boolean update(@RequestBody @Parameter(description="购物车表主键")Cart cart) {
        return cartService.updateById(cart);
    }

    /**
     * 查询所有购物车表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有购物车表")
    public List<Cart> list() {
        return cartService.list();
    }

    /**
     * 根据购物车表主键获取详细信息。
     *
     * @param id 购物车表主键
     * @return 购物车表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取购物车表")
    public Cart getInfo(@PathVariable Long id) {
        return cartService.getById(id);
    }

    /**
     * 分页查询购物车表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询购物车表")
    public Page<Cart> page(@Parameter(description="分页信息")Page<Cart> page) {
        return cartService.page(page);
    }

}
