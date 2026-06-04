package org.example.mq;

import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
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
        Long fkUserId = orderMessage.getFkUserId();// 用户id：谁参与了本次秒杀
        Long fkCourseId = orderMessage.getFkCourseId();// 课程id：秒杀的课程id
        Double price = orderMessage.getPrice();// 正常价格
        Double skPrice = orderMessage.getSkPrice();// 秒杀价格
        Order order = new Order();
        order.setSn(RandomUtil.randomNumbers(19));// 随机生成一个19位的订单编号
        order.setTotalAmount(skPrice);// 订单总金额
        order.setPayAmount(0.0);// 实际支付金额
        order.setPayType(ML.Order.NO_PAY);
        order.setStatus(ML.Order.UNPAID);
        order.setFkUserId(fkUserId);
        // 获取用户信息
        Result<User> userResult = userFeign.getInfo(fkUserId);
        // 错误处理
        if (userResult==null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"获取用户信息失败");
        }
        if (userResult.getData()==null){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"用户不存在");
        }
        order.setUsername(userResult.getData().getUsername());// 添加用户名到订单对象
        order.setInfo("秒杀活动订单");
        order.setCreated(LocalDateTime.now());
        order.setUpdated(LocalDateTime.now());
        orderMapper.insert(order);// 保存订单
        // 添加订单明细
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setFkOrderId(order.getId());
        orderDetail.setFkCourseId(fkCourseId);
        // 远程调用查询课程信息
        Result<Course> courseResult = courseFeign.getInfo(fkCourseId);
        if (courseResult==null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"获取课程信息失败");
        }
        Course course = courseResult.getData();
        if (course==null){
            throw new ServiceException(ResultCode.COURSE_NOT_FOUND,"课程不存在");
        }
        orderDetail.setCourseTitle(course.getTitle());// 课程标题
        orderDetail.setCourseCover(course.getCover());// 课程封面
        orderDetail.setCoursePrice(course.getPrice());// 课程价格
        orderDetail.setCreated(LocalDateTime.now());
        orderDetail.setUpdated(LocalDateTime.now());
        // 保存订单明细
        orderDetailMapper.insert(orderDetail);
    }
}
