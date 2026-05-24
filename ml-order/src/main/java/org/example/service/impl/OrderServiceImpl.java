package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.constant.ML;
import org.example.dto.OrderInsertDTO;
import org.example.dto.OrderPageDTO;
import org.example.dto.PrePayDTO;
import org.example.entity.*;
import org.example.exception.ServiceException;
import org.example.feign.CourseFeign;
import org.example.feign.UserFeign;
import org.example.mapper.CartMapper;
import org.example.mapper.OrderDetailMapper;
import org.example.mapper.OrderMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.example.service.OrderService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static org.example.entity.table.CartTableDef.CART;
import static org.example.entity.table.OrderDetailTableDef.ORDER_DETAIL;
import static org.example.entity.table.OrderTableDef.ORDER;

/**
 * 订单表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>  implements OrderService{

    @Resource
    private UserFeign userFeign;

    // 注入订单详情Mapper
    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private CourseFeign courseFeign;// 远程调用课程微服务，用于查询课程信息

    @Resource
    private CartMapper cartMapper;// 购物车Mapper,用于删除购物车

    @Override
    public boolean checkPay(String sn) {
        int payStatus = QueryChain.of(mapper)
                .select(ORDER.STATUS)// 查询订单状态
                .where(ORDER.SN.eq(sn))// 订单编号做条件
                .objAs(Integer.class);// 转换为Integer
        return payStatus == ML.Order.PAID;// 判断订单状态是否为已支付
    }

    @Override
    public boolean updateStatus(String sn, Integer status) {
        return UpdateChain.of(mapper)
                .set(ORDER.STATUS,status)// 修改订单状态
                .set(ORDER.UPDATED, LocalDateTime.now())
                .where(ORDER.SN.eq(sn))// 订单编号做条件
                .update();// 执行修改
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String prePay(PrePayDTO dto) {
        // 获取用户ID
        Long fkUserId = dto.getFkUserId();
        // 获取课程id列表
        List<Long> courseIds = dto.getCourseIds();
        // 查询用户的订单，防止出现重复购买现象
        List<Long> orderIds = QueryChain.of(mapper)
                .select(ORDER.ID)
                .where(ORDER.FK_USER_ID.eq(fkUserId))
                .listAs(Long.class);
        // 判断订单ID列表是否为空，看是否购买过课程
        if (CollUtil.isNotEmpty(orderIds)){
            List<Long> purchasedOrderIds = QueryChain.of(orderDetailMapper)
                    // 从订单明细表中查询用户已经购买过的课程ID
                    .select(ORDER_DETAIL.FK_COURSE_ID)
                    .where(ORDER_DETAIL.FK_ORDER_ID.in(orderIds))
                    .listAs(Long.class);
            // 用户已经购买的课程和这次下单的课程ID去交集
            purchasedOrderIds.retainAll(courseIds);
            if (CollUtil.isNotEmpty(purchasedOrderIds)){
                // 抛出异常，提示用户已经购买过这些课程
                throw new ServiceException(ResultCode.ORDER_DETAIL_REPEAT,"用户已经购买过部分或全部课程，无需重复下单");
            }
        }
        // 创建订单
        Order order = BeanUtil.copyProperties(dto,Order.class);
        String sn = RandomUtil.randomNumbers(19);// 随机生成一个19位的订单编号
        order.setSn(sn);// 设置订单编号
        order.setPayType(ML.Order.NO_PAY);// 设置支付类型为未支付
        order.setStatus(ML.Order.UNPAID);// 设置订单状态为未支付
        order.setPayAmount(0.0);// 设置支付金额
        Result<User> userResult = userFeign.getInfo(fkUserId);// 远程调用用户服务，获取用户信息
        if (userResult==null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"远程调用用户服务失败");
        }
        User user = userResult.getData();
        if (user==null){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"用户不存在");
        }
        order.setUsername(user.getUsername());// 添加用户名到订单对象
        // 保存订单
        if (!save(order)){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"订单保存失败");
        }
        // 批量保存订单明细
        Long orderId = order.getId();// 获取订单id
        //List<OrderDetail> orderDetails = new ArrayList<>();
        List<OrderDetail> orderDetails = courseIds.stream()
                .map(courseId -> {
                    Result<Course> courseResult = courseFeign.getInfo(courseId);// 远程调用课程服务，获取课程信息
                    // 需要做非空判断，但是我们省略不写了
                    Course course = courseResult.getData();
                    return OrderDetail.builder()
                            .fkOrderId(orderId)
                            .fkCourseId(courseId)
                            .courseTitle(course.getTitle())// 课程标题
                            .courseCover(course.getCover())// 课程封面
                            .coursePrice(course.getPrice())// 课程价格
                            .build();
                }).toList();
        // 批量保存订单明细
        orderDetailMapper.insertBatch(orderDetails);
        // 删除购物车中的课程
        UpdateChain.of(Cart.class)
                .where(CART.FK_USER_ID.eq(fkUserId))
                .and(CART.FK_COURSE_ID.in(courseIds))
                .remove();
        // 返回订单编号
        return sn;
    }

    @Override
    public boolean save(OrderInsertDTO dto) {
        // 获取用户id
        Long fkUserId = dto.getFkUserId();
        // 远程调用用户服务，获取用户信息
        Result<User> userResult = userFeign.getInfo(fkUserId);
        if (userResult== null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"远程调用用户服务失败");
        }
        User user = userResult.getData();
        if(user==null){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"用户不存在");
        }
        // 拷贝订单属性
        Order order = BeanUtil.copyProperties(dto,Order.class);
        order.setSn(RandomUtil.randomNumbers(19));// 随机生成一个19位的订单编号
        order.setUsername(user.getUsername());
        // 设置创建时间和修改时间
        order.setCreated(LocalDateTime.now());
        order.setUpdated(LocalDateTime.now());
        return mapper.insert(order)>0;
    }

    @Override
    public PageVO<Order> page(OrderPageDTO dto) {
        QueryChain<Order> queryChain = QueryChain.of(mapper)
                .orderBy(ORDER.UPDATED.desc());
        // 获取订单编号
        String sn = dto.getSn();
        if (sn!=null){
            queryChain.where(ORDER.SN.eq(sn));
        }
        // 获取订单状态
        Integer status = dto.getStatus();
        if (status!=null){
            queryChain.where(ORDER.STATUS.eq(status));
        }
        // 获取用户名
        String username = dto.getUsername();
        if (username!=null){
            queryChain.where(ORDER.USERNAME.eq(username));
        }
        Page<Order> result = queryChain.withRelations().page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Order> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        // 先删除订单详情
        UpdateChain.of(orderDetailMapper)
                .where(ORDER_DETAIL.FK_ORDER_ID.eq(id))
                .remove();
        // 再删除订单
        return super.removeById(id);
    }

    @Override
    public Order getById(Serializable id) {
        // 查询订单的同时查询订单详情
        return mapper.selectOneWithRelationsById(id);
    }
}
