package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.OrderInsertDTO;
import org.example.dto.OrderPageDTO;
import org.example.dto.PrePayDTO;
import org.example.entity.Order;
import org.example.vo.PageVO;

import java.io.Serializable;

/**
 * 订单表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface OrderService extends IService<Order> {

    boolean save(OrderInsertDTO dto);// 创建订单
    PageVO<Order> page(OrderPageDTO dto);// 订单分页

    boolean removeById(Serializable id);

    // 新增一个预支付订单的方法
    String prePay(PrePayDTO dto);

    // 更新订单状态
    boolean updateStatus(String sn, Integer status);

    // 检查订单是否完成支付
    boolean checkPay(String sn);
}
