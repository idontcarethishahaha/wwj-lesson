package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.CartInsertDTO;
import org.example.dto.CartPageDTO;
import org.example.entity.Cart;
import org.example.vo.PageVO;

/**
 * 购物车表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface CartService extends IService<Cart> {
    boolean save(CartInsertDTO dto);
    PageVO<Cart> page(CartPageDTO dto);

    // 清空购物车
    boolean clear(Long userId);
}
