package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.SeckillDetailInsertDTO;
import org.example.dto.SeckillDetailPageDTO;
import org.example.entity.Course;
import org.example.entity.SeckillDetail;
import org.example.exception.ServiceException;
import org.example.feign.CourseFeign;
import org.example.mapper.SeckillDetailMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.example.service.SeckillDetailService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.example.entity.table.SeckillDetailTableDef.SECKILL_DETAIL;

/**
 * 秒杀明细表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class SeckillDetailServiceImpl extends ServiceImpl<SeckillDetailMapper, SeckillDetail>  implements SeckillDetailService{

    @Resource
    private CourseFeign courseFeign;// 调用课程服务

    @Override
    public boolean save(SeckillDetailInsertDTO dto) {
        Long fkCourseId = dto.getFkCourseId();
        Result<Course> result = courseFeign.selectById(fkCourseId);
        if(result==null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"远程调用课程服务失败");
        }
        // 获取课程信息
        Course course = result.getData();
        if(course==null){
            throw new ServiceException(ResultCode.COURSE_NOT_FOUND,"课程不存在");
        }
        // 获取课程标题
        String courseTitle = course.getTitle();
        // 判断这个课程是否已经出现在当前的秒杀活动中，如果已存在，则不允许添加
        if (QueryChain.of(mapper)
                .where(SECKILL_DETAIL.FK_COURSE_ID.eq(fkCourseId))
                .and(SECKILL_DETAIL.FK_SECKILL_ID.eq(dto.getFkSeckillId()))
                .exists()){
            throw new ServiceException(ResultCode.SECKILL_DETAIL_REPEAT,"秒杀商品明细重复");
        }
        // 拷贝属性
        SeckillDetail seckillDetail = BeanUtil.copyProperties(dto, SeckillDetail.class);
        seckillDetail.setCourseTitle(courseTitle);//课程标题
        seckillDetail.setCourseCover(course.getCover());//课程封面
        seckillDetail.setCoursePrice(course.getPrice());//课程价格
        seckillDetail.setInfo(course.getInfo());//课程描述
        // 设置创建时间和修改时间
        seckillDetail.setCreated(LocalDateTime.now());
        seckillDetail.setUpdated(LocalDateTime.now());
        // 保存数据
        return mapper.insert(seckillDetail) > 0;
    }

    @Override
    public PageVO<SeckillDetail> page(SeckillDetailPageDTO dto) {
        QueryChain<SeckillDetail> queryChain = QueryChain.of(mapper);
        // 获取 skill_id
        Long fkSeckillId = dto.getSeckillId();
        if (fkSeckillId != null){
            queryChain.where(SECKILL_DETAIL.FK_SECKILL_ID.eq(fkSeckillId));
        }
        // 获取课程标题
        String courseTitle = dto.getCourseTitle();
        if (courseTitle!=null){
            queryChain.where(SECKILL_DETAIL.COURSE_TITLE.like(courseTitle));
        }
        // 分页查询转VO
        Page<SeckillDetail> result = queryChain.page(new Page(dto.getPageNum(), dto.getPageSize()));
        PageVO<SeckillDetail> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }
}
