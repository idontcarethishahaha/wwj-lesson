package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Seckill;
import org.example.mapper.SeckillMapper;
import org.example.service.SeckillService;
import org.springframework.stereotype.Service;

/**
 * 秒杀表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill>  implements SeckillService{

}
