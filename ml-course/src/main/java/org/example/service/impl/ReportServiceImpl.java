package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.ReportInsertDTO;
import org.example.dto.ReportPageDTO;
import org.example.entity.Report;
import org.example.entity.User;
import org.example.exception.ServiceException;
import org.example.feign.UserFeign;
import org.example.mapper.ReportMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.example.service.ReportService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import static org.example.entity.table.ReportTableDef.REPORT;

/**
 * 举报表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report>  implements ReportService{

    // 用户服务，用于获取举报用户信息
    @Resource
    private UserFeign userFeign;

    @Override
    public boolean insert(ReportInsertDTO dto) {
        // 举报视频
        // 获取举报用户的id
        Long fkUserId = dto.getFkUserId();
        Report report = BeanUtil.copyProperties(dto, Report.class);
        Result<User> userResult = userFeign.selectById(fkUserId);
        // 判断 userResult是否为空
        if(userResult == null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"获取用户信息失败");
        }
        User user = userResult.getData();
        // 判断 user是否为空
        if(user == null){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"该用户不存在");
        }
        report.setNickname(user.getNickname());//设置昵称
        if(mapper.insert(report) == 0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"添加举报数据失败");
        }
        return true;
    }

    @Override
    public PageVO<Report> page(ReportPageDTO dto) {
        // 添加关联查询集次信息
        RelationManager.addQueryRelations("episode");
        QueryChain<Report> queryChain = QueryChain.of(mapper);
        // 判断集次的id是否存在
        if (dto.getFkEpisodeId()!=null){
            queryChain.where(REPORT.FK_EPISODE_ID.eq(dto.getFkEpisodeId()));
        }
        // 判断用户id是否存在
        if (dto.getFkUserId()!=null){
            queryChain.where(REPORT.FK_USER_ID.eq(dto.getFkUserId()));
        }
        Page<Report> page = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Report> pageVO = new PageVO<>();
        BeanUtil.copyProperties(page,pageVO);
        pageVO.setPageNum(page.getPageNumber());
        return pageVO;
    }
}
