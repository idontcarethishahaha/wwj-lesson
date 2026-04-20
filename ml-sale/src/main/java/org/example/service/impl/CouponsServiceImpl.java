package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Coupons;
import org.example.mapper.CouponsMapper;
import org.example.service.CouponsService;
import org.springframework.stereotype.Service;

/**
 * 优惠卷表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CouponsServiceImpl extends ServiceImpl<CouponsMapper, Coupons>  implements CouponsService{

}
