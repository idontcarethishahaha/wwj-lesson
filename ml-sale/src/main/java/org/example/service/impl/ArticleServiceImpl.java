package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Article;
import org.example.mapper.ArticleMapper;
import org.example.service.ArticleService;
import org.springframework.stereotype.Service;

/**
 * 新闻表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>  implements ArticleService{

}
