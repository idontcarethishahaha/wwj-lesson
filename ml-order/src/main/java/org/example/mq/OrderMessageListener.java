package org.example.mq;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.example.constant.ML;
import org.example.dto.OrderMessage;
import org.example.entity.Course;
import org.example.entity.Order;
import org.example.entity.OrderDetail;
import org.example.entity.User;
import org.example.exception.ServiceException;
import org.example.feign.CourseFeign;
import org.example.feign.UserFeign;
import org.example.mapper.OrderDetailMapper;
import org.example.mapper.OrderMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 类说明：订单消息处理的监听器
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-17 14:06
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "ml-topic",// 主题
        selectorExpression = "ml-tag",// 标签过滤
        consumerGroup = "ml-consumer-group",// 消费者组
        consumeMode = ConsumeMode.CONCURRENTLY,// 并发消费
        messageModel = MessageModel.CLUSTERING// 集群模式
)
public class OrderMessageListener implements RocketMQListener<OrderMessage> {
    @Resource
    private UserFeign userFeign;// 远程调用用户微服务，用于查询用户信息

    @Resource
    private CourseFeign courseFeign;// 远程调用课程微服务，用于查询课程信息

    @Resource
    private OrderMapper orderMapper;// 订单Mapper,用于保存订单

    @Resource
    private OrderDetailMapper orderDetailMapper;// 订单详情Mapper,用于保存订单详情

    // ctrl + i
    @Override
    public void onMessage(OrderMessage orderMessage) {
        log.info("========== 收到秒杀订单消息 ==========");
        log.info("消息内容: {}", orderMessage);
        try {
            Long fkUserId = orderMessage.getFkUserId();// 用户id：谁参与了本次秒杀
            Long fkCourseId = orderMessage.getFkCourseId();// 课程id：秒杀的课程id
            Double price = orderMessage.getPrice();// 正常价格
            Double skPrice = orderMessage.getSkPrice();// 秒杀价格
            
            log.info("开始创建订单...");
            Order order = new Order();
            order.setSn(RandomUtil.randomNumbers(19));// 随机生成一个19位的订单编号
            order.setTotalAmount(skPrice);// 订单总金额
            order.setPayAmount(0.0);// 实际支付金额
            order.setPayType(ML.Order.NO_PAY);
            order.setStatus(ML.Order.UNPAID);
            order.setFkUserId(fkUserId);
            order.setInfo("秒杀活动订单");
            order.setCreated(LocalDateTime.now());
            order.setUpdated(LocalDateTime.now());
            order.setVersion(1L); // 设置乐观锁初始版本
            order.setDeleted(0); // 设置逻辑删除标志
            
            log.info("获取用户信息, fkUserId: {}", fkUserId);
            Result<User> userResult = userFeign.getInfo(fkUserId);
            if (userResult == null) {
                log.error("获取用户信息失败，userResult 为 null");
                throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR, "获取用户信息失败");
            }
            if (userResult.getData() == null) {
                log.error("获取用户信息失败，userResult.getData() 为 null");
                throw new ServiceException(ResultCode.USER_NOT_FOUND, "用户不存在");
            }
            order.setUsername(userResult.getData().getUsername());
            
            log.info("保存订单...");
            orderMapper.insert(order);// 保存订单
            log.info("订单保存成功，订单ID: {}", order.getId());
            
            log.info("开始创建订单明细...");
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setFkOrderId(order.getId());
            orderDetail.setFkCourseId(fkCourseId);
            orderDetail.setCreated(LocalDateTime.now());
            orderDetail.setUpdated(LocalDateTime.now());
            
            log.info("获取课程信息, fkCourseId: {}", fkCourseId);
            Result<Course> courseResult = courseFeign.getInfo(fkCourseId);
            if (courseResult == null) {
                log.error("获取课程信息失败，courseResult 为 null");
                throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR, "获取课程信息失败");
            }
            Course course = courseResult.getData();
            if (course == null) {
                log.error("获取课程信息失败，courseResult.getData() 为 null");
                throw new ServiceException(ResultCode.COURSE_NOT_FOUND, "课程不存在");
            }
            orderDetail.setCourseTitle(course.getTitle());// 课程标题
            orderDetail.setCourseCover(course.getCover());// 课程封面
            orderDetail.setCoursePrice(course.getPrice());// 课程价格
            
            log.info("保存订单明细...");
            orderDetailMapper.insert(orderDetail);
            log.info("订单明细保存成功");
            log.info("========== 秒杀订单创建完成 ==========");
        } catch (Exception e) {
            log.error("========== 处理秒杀订单消息异常 ==========", e);
            throw e; // 继续抛出异常，让 RocketMQ 重试
        }
    }
}
