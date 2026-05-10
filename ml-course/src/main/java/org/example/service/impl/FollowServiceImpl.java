package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.FollowInsertDTO;
import org.example.dto.FollowPageDTO;
import org.example.entity.Follow;
import org.example.entity.User;
import org.example.exception.ServiceException;
import org.example.feign.UserFeign;
import org.example.mapper.FollowMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.example.service.FollowService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import static org.example.entity.table.FollowTableDef.FOLLOW;

/**
 * 收藏表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>  implements FollowService{
    @Resource
    private UserFeign userFeign;//远程调用

    @Override
    public boolean insert(FollowInsertDTO dto) {
        Long fkUserId = dto.getFkUserId();
        Result<User> userResult = userFeign.selectById(fkUserId);
        if(userResult == null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"获取用户信息失败");
        }
        User user = userResult.getData();
        if (user == null){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"该用户不存在");
        }
        Follow follow = BeanUtil.copyProperties(dto, Follow.class);
        follow.setNickname(user.getNickname());
        if(mapper.insert(follow)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"添加收藏数据失败");
        }
        return true;
    }

    @Override
    public PageVO<Follow> page(FollowPageDTO dto) {
        RelationManager.addQueryRelations("episode");
        Long fkUserId = dto.getFkUserId();
        Long fkEpisodeId = dto.getFkEpisodeId();
        QueryChain queryChain = QueryChain.of(mapper);
        if (fkUserId != null){
            queryChain.where(FOLLOW.FK_USER_ID.eq(fkUserId));
        }
        if(fkEpisodeId != null){
            queryChain.where(FOLLOW.FK_EPISODE_ID.eq(fkEpisodeId));
        }
        Page<Follow> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Follow> pageVO = new PageVO<>();
        pageVO.setPageNum(result.getPageNumber());
        BeanUtil.copyProperties(result,pageVO);
        return pageVO;
    }
}
