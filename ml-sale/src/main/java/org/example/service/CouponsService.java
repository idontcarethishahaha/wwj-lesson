package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.CouponsInsertDTO;
import org.example.dto.CouponsPageDTO;
import org.example.entity.Coupons;
import org.example.vo.PageVO;

import java.util.List;

/**
 * 优惠卷表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface CouponsService extends IService<Coupons> {
    boolean insert(CouponsInsertDTO dto);
    PageVO<Coupons> page(CouponsPageDTO dto);
    Coupons selectByCode(String code);//每个优惠券设置口令

    // 查询全部可用优惠券
    List<Coupons> queryAvailableCoupons();
}
