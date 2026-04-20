package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Cart;
import org.example.mapper.CartMapper;
import org.example.service.CartService;
import org.springframework.stereotype.Service;

/**
 * 购物车表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart>  implements CartService{

}
