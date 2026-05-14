package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.OrderDetailInsertDTO;
import org.example.dto.OrderDetailPageDTO;
import org.example.entity.OrderDetail;
import org.example.vo.PageVO;

/**
 * 订单明细表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface OrderDetailService extends IService<OrderDetail> {

    boolean save(OrderDetailInsertDTO dto);
    PageVO<OrderDetail> page(OrderDetailPageDTO dto);
}
