package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.OrderDetail;
import org.example.mapper.OrderDetailMapper;
import org.example.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * 订单明细表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>  implements OrderDetailService{

}
