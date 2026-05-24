package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.CartInsertDTO;
import org.example.dto.CartPageDTO;
import org.example.entity.Cart;
import org.example.service.CartService;
import org.example.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("insert")
    @Operation(description="保存购物车表")
    public boolean save(@RequestBody @Parameter(description="购物车表") CartInsertDTO cart) {
        return cartService.save(cart);
    }

    /**
     * 根据主键删除购物车表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("delete/{id}")
    @Operation(description="根据主键购物车表")
    public boolean remove(@PathVariable("id") @Parameter(description="购物车表主键")Long id) {
        return cartService.removeById(id);
    }

    /**
     * 根据主键批量删除购物车表。
     *
     * @param ids 主键列表
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("deleteBatch")
    @Operation(description="根据主键批量删除购物车表")
    public boolean removeBatch(@RequestParam("ids") @Parameter(description="购物车表主键列表")List<Long> ids) {
        return cartService.removeByIds(ids);
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
    @GetMapping("select/{id}")
    @Operation(description="根据主键获取购物车表")
    public Cart getInfo(@PathVariable("id") Long id) {
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
    public PageVO<Cart> page(@Parameter(description="分页信息") CartPageDTO page) {
        return cartService.page(page);
    }

    /**
     * 清空购物车表。
     *
     * @param userId 用户id
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("clear/{userId}")
    @Operation(description="清空购物车表")
    public boolean clear(@PathVariable("userId") @Parameter(description="用户id")Long userId) {
        return cartService.clear(userId);
    }

}
