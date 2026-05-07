package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.CommentInsertDTO;
import org.example.dto.CommentPageDTO;
import org.example.entity.Comment;
import org.example.vo.PageVO;

/**
 * 评论表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface CommentService extends IService<Comment> {
   boolean insert(CommentInsertDTO dto);
   PageVO<Comment> page(CommentPageDTO dto);
}
