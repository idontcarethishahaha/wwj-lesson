package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.OrderDetailInsertDTO;
import org.example.dto.OrderDetailPageDTO;
import org.example.entity.Course;
import org.example.entity.OrderDetail;
import org.example.exception.ServiceException;
import org.example.feign.CourseFeign;
import org.example.mapper.OrderDetailMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.example.service.OrderDetailService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import static org.example.entity.table.OrderDetailTableDef.ORDER_DETAIL;

/**
 * 订单明细表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>  implements OrderDetailService{

    @Resource
    private CourseFeign courseFeign;
    @Override
    public boolean save(OrderDetailInsertDTO dto) {
        Long fkCourseId = dto.getFkCourseId();
        Long fkOrderId = dto.getFkOrderId();
        if (QueryChain.of(mapper)
                .where(ORDER_DETAIL.FK_COURSE_ID.eq(fkCourseId))
                .and(ORDER_DETAIL.FK_ORDER_ID.eq(fkOrderId))
                .exists()){
            throw new ServiceException(ResultCode.ORDER_DETAIL_REPEAT,"订单明细已存在");
        }
        // 组装订单明细对象
        OrderDetail orderDetail = BeanUtil.copyProperties(dto,OrderDetail.class);
        // 远程调用查询课程信息
        Result<Course> courseResult = courseFeign.getInfo(fkCourseId);
        if (courseResult==null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"远程调用课程服务失败");
        }
        Course course = courseResult.getData();
        if (course==null){
            throw new ServiceException(ResultCode.COURSE_NOT_FOUND,"课程不存在");
        }
        orderDetail.setCourseTitle(course.getTitle());//课程标题
        orderDetail.setCourseCover(course.getCover());//课程封面
        orderDetail.setCoursePrice(course.getPrice());//课程价格
        return mapper.insert(orderDetail)>0;
    }

    @Override
    public PageVO<OrderDetail> page(OrderDetailPageDTO dto) {
        QueryChain<OrderDetail> queryChain = QueryChain.of(mapper);
        // 获取课程标题
        String courseTitle = dto.getCourseTitle();
        if (courseTitle!=null){
            queryChain.where(ORDER_DETAIL.COURSE_TITLE.like(courseTitle));
        }
        // 分页查询转VO
        Page<OrderDetail> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<OrderDetail> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }
}
