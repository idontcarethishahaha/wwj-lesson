package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.example.component.MyRedis;
import org.example.constant.ML;
import org.example.dto.KillDTO;
import org.example.dto.OrderMessage;
import org.example.dto.SeckillInsertDTO;
import org.example.dto.SeckillPageDTO;
import org.example.entity.Seckill;
import org.example.exception.ServiceException;
import org.example.mapper.SeckillMapper;
import org.example.result.ResultCode;
import org.example.service.SeckillService;
import org.example.vo.PageVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mybatisflex.core.query.QueryMethods.curDate;
import static com.mybatisflex.core.query.QueryMethods.date;
import static org.example.entity.table.SeckillTableDef.SECKILL;

/**
 * 秒杀表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill>  implements SeckillService{
    @Override
    public boolean save(SeckillInsertDTO dto) {
        // 获取title
        String title = dto.getTitle();
        // 获取活动开始时间
        LocalDateTime startTime = dto.getStartTime();
        // 判断秒杀活动是否重复
        if (QueryChain.of(mapper)
                .where(SECKILL.TITLE.eq(title))
                .and(SECKILL.START_TIME.eq(startTime))
                .exists()){
            throw new ServiceException(ResultCode.REPEAT_RECORD,"该秒杀活动已存在，请勿重复添加");
        }
        Seckill seckill = BeanUtil.copyProperties(dto,Seckill.class);
        // 判断info是否为空
        if (StrUtil.isBlank(seckill.getInfo())){
            seckill.setInfo("暂无描述");
        }
        // 设置创建时间和修改时间
        seckill.setCreated(LocalDateTime.now());
        seckill.setUpdated(LocalDateTime.now());
        return mapper.insert(seckill)>0;
    }

    @Override
    public PageVO<Seckill> page(SeckillPageDTO dto) {
        QueryChain<Seckill> queryChain = QueryChain.of(mapper);
        // 判断是否提供了title
        if(StrUtil.isNotBlank(dto.getTitle())){
            queryChain.where(SECKILL.TITLE.like(dto.getTitle()));
        }
        // 分页查询转VO
        Page<Seckill> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Seckill> pageVO = new PageVO<>();
        pageVO.setPageNum(result.getPageNumber());
        BeanUtil.copyProperties(result,pageVO);
        return pageVO;
    }

    @Override
    public List<Seckill> queryTodaySeckill() {
        // 查询今天开始的秒杀活动
        return QueryChain.of(mapper)
                .where(date(SECKILL.START_TIME).eq(curDate()))
                .orderBy(SECKILL.START_TIME.asc())
                .withRelations()
                .list();
    }

    @Resource
    private MyRedis myRedis;

    // 专门用来操作Redis数据库的客户端
    @Resource
    private RedissonClient redissonClient;

    // 注入一个RocketMQTemplate
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    // 秒杀
    @Override
    public boolean kill(KillDTO dto) {
        // 获取秒杀活动ID
        Long fkSeckillId = dto.getFkSeckillId();
        Seckill seckill = mapper.selectOneById(fkSeckillId);
        if (seckill == null){
            throw new ServiceException(ResultCode.SECKILL_NOT_FOUND,"秒杀活动不存在");
        }
        // 1 判断秒杀活动是否在有效时间内
        // 判断秒杀活动是否开始
        if (seckill.getStartTime().isAfter(LocalDateTime.now())){
            throw new ServiceException(ResultCode.SECKILL_NOT_START,"秒杀活动未开始");
        }
        // 判断秒杀活动是否结束
        if (seckill.getEndTime().isBefore(LocalDateTime.now())){
            throw new ServiceException(ResultCode.SECKILL_END,"秒杀活动已结束");
        }
        // 2 获取redis中的分布式锁
        final String KEY = ML.Redis.SECKILL_COURSE_COUNT_PREFIX + fkSeckillId;
        // 只有获得分布式锁的用户才能继续秒杀活动（防止超卖）
        RLock lock = redissonClient.getLock("skLock");
        // 获取锁，并锁定10秒
        lock.lock(10, TimeUnit.SECONDS);
        try {
            // 3 判断库存是否充足
            if (Integer.parseInt(myRedis.get(KEY))>0){
                // 4 减库存
                myRedis.incr(KEY,-1);
                // 5 发送消息给MQ，告诉MQ该用户秒杀成功，异步下单（创建订单）：利用MQ的削峰填谷，快速完成秒杀流程，提升秒杀活动成功率
                OrderMessage orderMessage = new OrderMessage();
                orderMessage.setFkUserId(dto.getFkUserId());//用户id：谁参与了本次秒杀
                orderMessage.setFkCourseId(dto.getFkCourseId());// 课程id：秒杀的课程id
                orderMessage.setPrice(dto.getPrice());//正常价格
                orderMessage.setSkPrice(dto.getSkPrice());//秒杀价格
                rocketMQTemplate.convertAndSend("ml-topic:ml-tag",orderMessage);
                return true;
            }else{
                throw new ServiceException(ResultCode.SERVER_ERROR,"库存不足");
            }

        }finally {
            lock.unlock();//为了避免锁忘记，一定要在finally中解锁
        }
    }
}
