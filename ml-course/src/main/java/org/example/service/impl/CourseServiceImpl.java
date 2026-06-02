package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.component.MyRedis;
import org.example.constant.ML;
import org.example.dto.CourseInsertDTO;
import org.example.dto.CoursePageDTO;
import org.example.dto.CourseUpdateDTO;
import org.example.entity.Course;
import org.example.exception.ServiceException;
import org.example.mapper.CourseMapper;
import org.example.mapper.EpisodeMapper;
import org.example.mapper.SeasonMapper;
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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static org.example.entity.table.CourseTableDef.COURSE;
import static org.example.entity.table.EpisodeTableDef.EPISODE;
import static org.example.entity.table.SeasonTableDef.SEASON;

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

    @Resource
    private SeasonMapper seasonMapper;

    @Resource
    private EpisodeMapper episodeMapper;

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
        // 设置创建时间和修改时间
        course.setCreated(LocalDateTime.now());
        course.setUpdated(LocalDateTime.now());
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
        Page<Course> result = queryChain.withRelations().page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Course> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

//    @Override
//    @Transactional
//    public String uploadCover(Long courseId, MultipartFile coverFile) {
//        // 先查询课程
//        Course course = QueryChain.of(mapper)
//                .where(COURSE.ID.eq(courseId))
//                .one();
//        // 判断课程是否存在
//        if(course==null){
//            throw new ServiceException(ResultCode.COURSE_NOT_FOUND,"课程不存在");
//        }
//        // 将新的封面文件上传到 MinIO,生成一个新的文件名
//        String newFileName = MinioUtil.upload(coverFile,ML.MinIO.COURSE_COVER_DIR,ML.MinIO.BUCKET_NAME);
//        course.setCover(newFileName);
//        if(mapper.update(course)<=0){
//            throw new ServiceException(ResultCode.MYSQL_ERROR,"更新数据库课程封面失败");
//        }
//        // 返回新的文件名
//        return newFileName;
//    }


    // new
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String uploadCover(Long id, MultipartFile newFile) {
        // 1. 校验课程是否存在
        Course course = mapper.selectOneById(id);
        if (ObjectUtil.isNull(course)) {
            throw new ServiceException(ResultCode.COURSE_NOT_FOUND, id + "号课程数据不存在");
        }

        // 2. 备份旧文件名
        String oldFileName = course.getCover();

        // 3. 生成新文件名并上传 MinIO
        String newFileName = null;
        try {
            // 上传新文件
            newFileName = MinioUtil.upload(newFile, ML.MinIO.COURSE_COVER_DIR, ML.MinIO.BUCKET_NAME);
            // 删除旧文件（默认封面不删）
            if (!ML.Course.DEFAULT_COVER.equals(oldFileName)) {
                MinioUtil.delete(oldFileName, ML.MinIO.COURSE_COVER_DIR, ML.MinIO.BUCKET_NAME);
            }
        } catch (Exception e) {
            throw new ServiceException(ResultCode.SERVER_ERROR, "MinIO操作失败：" + e.getMessage());
        }

        // 4. 【关键】MyBatis-Flex 按主键更新封面和时间
        boolean success = UpdateChain.of(Course.class)
                .set(COURSE.COVER, newFileName)
                .set(COURSE.UPDATED, LocalDateTime.now())
                .where(COURSE.ID.eq(id))
                .update();

        if (!success) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库更新失败，可能课程ID不存在");
        }

        return newFileName;
    }

    @Override
    public String uploadSummary(Long courseId, MultipartFile summaryFile) {
        // 先查询课程
        Course course = QueryChain.of(mapper)
                .where(COURSE.ID.eq(courseId))
                .one();
        // 判断课程是否存在
        if(course==null){
            throw new ServiceException(ResultCode.COURSE_NOT_FOUND,"课程不存在");
        }
        // 将新的封面文件上传到 MinIO,生成一个新的文件名
        String newFileName = MinioUtil.upload(summaryFile,ML.MinIO.COURSE_SUMMARY_DIR,ML.MinIO.BUCKET_NAME);
        course.setSummary(newFileName);
        if(mapper.update(course)<=0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"更新数据库课程摘要失败");
        }
        // 返回新的文件名
        return newFileName;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Long id) {

        // 检查课程是否存在
        this.existsById(id);

        // 通过课程主键查询全部季次的ID列表
        // select id from season where fk_course_id = ?
        List<Long> seasonIds = QueryChain.of(seasonMapper)
                .select(SEASON.ID)
                .where(SEASON.FK_COURSE_ID.eq(id))
                .objListAs(Long.class);

        // 存在季记录时，批量删除季
        this.clearSeasonAndEpisode(seasonIds);

        // 删除课程
        // delete from course where id = ?
        if (mapper.deleteById(id) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
        return true;
    }

    /**
     * 按主键检查课程是否存在，如果不存在则直接抛出异常
     *
     * @param id 课程主键
     */
    private void existsById(Long id) {
        // select count(*) from course where id = ?
        if (!QueryChain.of(mapper)
                .where(COURSE.ID.eq(id))
                .exists()) {
            throw new ServiceException(ResultCode.COURSE_NOT_FOUND, id + "号课程数据不存在");
        }
    }

    /**
     * 根据季次ID列表，清空全部季次记录及每个季次中的集次记录
     *
     * @param seasonIds 季次主键列表
     */
    private void clearSeasonAndEpisode(List<Long> seasonIds) {

        // 存在季次时，批量删除季次
        if (ObjectUtil.isNotEmpty(seasonIds)) {

            // 通过季次主键列表查询全部集次的ID列表
            // select id from episode where fk_season_id in (?)
            List<Long> episodeIds = QueryChain.of(episodeMapper)
                    .select(EPISODE.ID)
                    .where(EPISODE.FK_SEASON_ID.in(seasonIds))
                    .objListAs(Long.class);

            // 存在集次时，批量删除集次
            if (ObjectUtil.isNotEmpty(episodeIds)) {
                if (episodeMapper.deleteBatchByIds(episodeIds) <= 0) {
                    throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除集次数据失败");
                }
            }

            // 批量删除季次
            if (seasonMapper.deleteBatchByIds(seasonIds) <= 0) {
                throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除季次数据失败");
            }
        }
    }

    @Override
    public Course getById(Serializable id) {
        return mapper.selectOneWithRelationsById(id);
    }

    @Override
    public boolean update(CourseUpdateDTO dto) {
        String title = dto.getTitle();
        Long id = dto.getId();

        // 检查课程是否存在
        this.existsById(id);

        // 标题查重
        // select count(1) from course where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(COURSE.TITLE.eq(dto.getTitle()))
                .and(COURSE.ID.ne(dto.getId()))
                .exists()) {
            throw new ServiceException(ResultCode.TITLE_REPEAT, "标题" + title + "重复");
        }

        // 组装实体类
        Course course = BeanUtil.copyProperties(dto, Course.class);
        course.setUpdated(LocalDateTime.now());
        // update course set title = ?, author = ?, fk_category_id = ?, info = ?, summary = ?, cover = ?, price = ?, idx = ?, updated = ? where id = ?
        if (!UpdateChain.of(course)
                .where(COURSE.ID.eq(course.getId()))
                .update()) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库修改失败");
        }
        return true;
    }

    @Override
    public List<Course> recommend() {
        RelationManager.addQueryRelations("category", "seasons", "episode");
        return QueryChain.of(mapper)
                .orderBy(COURSE.IDX.asc(), COURSE.ID.desc())
                .limit(6)
                .withRelations()
                .list();
    }

}
