package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.OrderInsertDTO;
import org.example.dto.OrderPageDTO;
import org.example.entity.Order;
import org.example.entity.User;
import org.example.exception.ServiceException;
import org.example.feign.UserFeign;
import org.example.mapper.OrderDetailMapper;
import org.example.mapper.OrderMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.example.service.OrderService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

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

//    @Resource
//    private CourseFeign courseFeign;

    @Resource
    private UserFeign userFeign;

    // 注入订单详情Mapper
    @Resource
    private OrderDetailMapper orderDetailMapper;

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
        Page<Order> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
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
}
