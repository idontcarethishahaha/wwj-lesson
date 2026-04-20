package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Comment;
import org.example.mapper.CommentMapper;
import org.example.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * 评论表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>  implements CommentService{

}
