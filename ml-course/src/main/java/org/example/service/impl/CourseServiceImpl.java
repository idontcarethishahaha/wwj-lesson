package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.component.MyRedis;
import org.example.constant.ML;
import org.example.dto.CourseInsertDTO;
import org.example.dto.CoursePageDTO;
import org.example.entity.Course;
import org.example.exception.ServiceException;
import org.example.mapper.CourseMapper;
import org.example.result.ResultCode;
import org.example.service.CourseService;
import org.example.service.EpisodeService;
import org.example.service.SeasonService;
import org.example.util.MinioUtil;
import org.example.vo.CourseSimpleListVO;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.example.entity.table.CourseTableDef.COURSE;

/**
 * 课程表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>  implements CourseService{

    @Resource
    private MyRedis myRedis;//Redis组件

    @Resource
    private SeasonService seasonService;// 季次

    @Resource
    private EpisodeService episodeService;// 集数

    @Override
    public boolean insert(CourseInsertDTO dto) {
        // 判断标题是否重复
        String title = dto.getTitle();
        if(QueryChain.of(mapper)
                .where(COURSE.TITLE.eq(title))
                .exists()){
            // 抛出异常
            throw new ServiceException(ResultCode.TITLE_REPEAT,"该标题已存在，请勿重复添加");
        }
        // 组装实体类，插入数据库
        Course course = BeanUtil.copyProperties(dto, Course.class);
        if(StrUtil.isBlank(course.getInfo())){
            course.setInfo("暂无描述");
        }
        // 设置默认的封面和摘要
        course.setSummary(ML.Course.DEFAULT_SUMMARY);
        course.setCover(ML.Course.DEFAULT_COVER);
        if(mapper.insert(course)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"数据操作失败");
        }
        return true;
    }

    @Override
    public List<CourseSimpleListVO> simpleList() {
        return QueryChain.of(mapper)
                .select(COURSE.ID,COURSE.TITLE,COURSE.PRICE)// 只查询指定的字段：id,title,price
                .orderBy(COURSE.IDX.asc(),COURSE.ID.desc())// 排序
                .listAs(CourseSimpleListVO.class);// 转换成指定的VO
    }

    @Override
    public PageVO<Course> page(CoursePageDTO dto) {
        // 指定要关联查询的字段：课程类别,季次,集数
        RelationManager.addQueryRelations("category","seasons","episode");
        QueryChain queryChain = QueryChain.of(mapper)
                .orderBy(COURSE.IDX.asc(),COURSE.ID.desc());
        // 判断title是否有值
        String title = dto.getTitle();
        if(StrUtil.isNotBlank(title)){
            queryChain.where(COURSE.TITLE.like(title));
        }
        // 判断课程类别是否有值
        Long fkCategoryId = dto.getFkCategoryId();
        if(fkCategoryId!=null){
            queryChain.where(COURSE.FK_CATEGORY_ID.eq(fkCategoryId));
        }
        // 分页查询
        Page<Course> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Course> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

    @Override
    @Transactional
    public String uploadCover(Long courseId, MultipartFile coverFile) {
        // 先查询课程
        Course course = QueryChain.of(mapper)
                .where(COURSE.ID.eq(courseId))
                .one();
        // 判断课程是否存在
        if(course==null){
            throw new ServiceException(ResultCode.COURSE_NOT_FOUND,"课程不存在");
        }
        // 生成一个新的文件名
        String newFileName = MinioUtil.randomFilename(coverFile);
        course.setCover(newFileName);
        if(mapper.update(course)<=0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"数据操作失败");
        }
        // 将新的封面文件上传到 MinIO
        MinioUtil.upload(coverFile,ML.MinIO.COURSE_COVER_DIR,ML.MinIO.BUCKET_NAME);
        // 返回新的文件名
        return newFileName;
    }
}
