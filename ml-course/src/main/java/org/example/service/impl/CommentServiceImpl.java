package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.CommentInsertDTO;
import org.example.dto.CommentPageDTO;
import org.example.entity.Comment;
import org.example.entity.User;
import org.example.exception.ServiceException;
import org.example.feign.UserFeign;
import org.example.mapper.CommentMapper;
import org.example.result.Result;
import org.example.result.ResultCode;
import org.example.service.CommentService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import static org.example.entity.table.CommentTableDef.COMMENT;

/**
 * 评论表 服务层实现
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>  implements CommentService{

    @Resource
    private UserFeign userFeign;

    @Override
    public boolean insert(CommentInsertDTO dto) {
        // 获取评论用户信息
        Result<User> userResult = userFeign.selectById(dto.getFkUserId());
        if (userResult == null){
            throw new ServiceException(ResultCode.OPEN_FEIGN_ERROR,"获取用户信息失败");
        }
        User user = userResult.getData();
        if (user == null){
            throw new ServiceException(ResultCode.USER_NOT_FOUND,"该用户不存在");
        }
        Comment comment = BeanUtil.copyProperties(dto, Comment.class);
        // 在添加评论时，将用户信息复制到评论对象中
        comment.setNickname(user.getNickname());
        comment.setAvatar(user.getAvatar());
        comment.setProvince(user.getProvince());
        if (mapper.insert(comment) == 0){
            throw new ServiceException(ResultCode.SERVER_ERROR,"添加评论失败");
        }
        return true;
    }

    @Override
    public PageVO<Comment> page(CommentPageDTO dto) {
        QueryChain queryChain = QueryChain.of(mapper);
        if (dto.getFkEpisodeId()!=null){
            queryChain.where(COMMENT.FK_EPISODE_ID.eq(dto.getFkEpisodeId()));
        }
        if (dto.getFkUserId()!=null){
            queryChain.where(COMMENT.FK_USER_ID.eq(dto.getFkUserId()));
        }
        Page<Comment> result = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        PageVO<Comment> pageVO = new PageVO<>();
        pageVO.setPageNum(result.getPageNumber());
        BeanUtil.copyProperties(result,pageVO);
        return pageVO;
    }
}
