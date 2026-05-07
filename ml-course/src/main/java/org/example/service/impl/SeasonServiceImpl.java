package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.dto.SeasonInsertDTO;
import org.example.dto.SeasonPageDTO;
import org.example.entity.Season;
import org.example.exception.ServiceException;
import org.example.mapper.SeasonMapper;
import org.example.result.ResultCode;
import org.example.service.SeasonService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import static org.example.entity.table.SeasonTableDef.SEASON;

/**
 * 季次表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class SeasonServiceImpl extends ServiceImpl<SeasonMapper, Season>  implements SeasonService{
    @Override
    public boolean insert(SeasonInsertDTO dto) {
        Season season = BeanUtil.copyProperties(dto, Season.class);
        // 判断 info 是否为空
        if(StrUtil.isBlank(season.getInfo())){
            season.setInfo("暂无描述");
        }
        // 插入
        if (mapper.insert(season)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"添加数据失败");
        }
        return true;
    }

    @Override
    public PageVO<Season> page(SeasonPageDTO dto) {
        QueryChain queryChain = QueryChain.of(mapper)
                .orderBy(SEASON.IDX.asc(),SEASON.ID.desc());
        // 判断 title 是否为空
        if(StrUtil.isNotBlank(dto.getTitle())){
            queryChain.where(SEASON.TITLE.like(dto.getTitle()));
        }
        // 判断fkCourseId（课程id）是否为空
        if(dto.getFkCourseId()!=null){
            queryChain.where(SEASON.FK_COURSE_ID.eq(dto.getFkCourseId()));
        }
        // 获取分页数据
        Page<Season> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Season> pageVO = new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

    @Override
    public Season getById(Long id) {
        // 关联查询
        RelationManager.addQueryRelations("course","episodes","category");
        Season season = mapper.selectOneWithRelationsById(id);
        if (season == null){
            // 数据不存在，抛出异常
            throw new ServiceException(ResultCode.SEASON_NOT_FOUND,"数据不存在");
        }
        return season;
    }
}
