package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.SeckillDetail;
import org.example.mapper.SeckillDetailMapper;
import org.example.service.SeckillDetailService;
import org.springframework.stereotype.Service;

/**
 * 秒杀明细表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class SeckillDetailServiceImpl extends ServiceImpl<SeckillDetailMapper, SeckillDetail>  implements SeckillDetailService{

}
