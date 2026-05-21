package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.CartInsertDTO;
import org.example.dto.CartPageDTO;
import org.example.entity.Cart;
import org.example.entity.Course;
import org.example.entity.User;
import org.example.exception.ServiceException;
import org.example.feign.CourseFeign;
import org.example.feign.UserFeign;
import org.example.mapper.CartMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.example.service.CartService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.example.entity.table.CartTableDef.CART;

/**
 * 购物车表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart>  implements CartService{

    @Resource
    private CourseFeign courseFeign;// 调用课程服务

    @Resource
    private UserFeign userFeign;// 调用用户服务

    @Override
    public boolean save(CartInsertDTO dto) {
        // 分别获取课程id和用户id
        Long fkCourseId = dto.getFkCourseId();
        Long fkUserId = dto.getFkUserId();
        if (QueryChain.of(mapper)
                .where(CART.FK_COURSE_ID.eq(fkCourseId))
                .and(CART.FK_USER_ID.eq(fkUserId))
                .exists()){
            throw new ServiceException(ResultCode.CART_REPEAT, "该用户已添加该课程");
        }
        // 拷贝购物车属性
        Cart cart = BeanUtil.copyProperties(dto,Cart.class);
        // 远程调用用户服务
        Result<User> userResult = userFeign.getInfo(fkUserId);
        if (userResult==null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR, "远程调用用户服务失败");
        }
        User user = userResult.getData();
        if(user==null){
            throw new ServiceException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }
        // 添加用户名
        cart.setUsername(user.getUsername());
        // 远程调用课程服务
        Result<Course> courseResult = courseFeign.getInfo(fkCourseId);
        if (courseResult==null){
            throw new ServiceException(ResultCode.COURSE_NOT_FOUND, "课程不存在");
        }
        Course course = courseResult.getData();
        // 添加课程属性
        cart.setCourseTitle(course.getTitle());
        cart.setCourseCover(course.getCover());
        cart.setCoursePrice(course.getPrice());
        // 设置创建时间和修改时间
        cart.setCreated(LocalDateTime.now());
        cart.setUpdated(LocalDateTime.now());
        // 保存购物车
        return mapper.insert(cart)>0;
    }

    @Override
    public PageVO<Cart> page(CartPageDTO dto) {
        QueryChain<Cart> queryChain = QueryChain.of(mapper);
        // 获取课程id和用户id
        Long fkCourseId = dto.getFkCourseId();
        Long fkUserId = dto.getFkUserId();
        if (fkCourseId != null){
            queryChain.where(CART.FK_COURSE_ID.eq(fkCourseId));
        }
        if (fkUserId != null){
            queryChain.where(CART.FK_USER_ID.eq(fkUserId));
        }
        // 分页查询
        Page<Cart> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Cart> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

    @Override
    public boolean clear(Long userId) {
        return UpdateChain.of(mapper)
                .where(CART.FK_USER_ID.eq(userId))
                .remove();
    }
}
