package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.ArticleInsertDTO;
import org.example.dto.ArticlePageDTO;
import org.example.entity.Article;
import org.example.vo.PageVO;

import java.util.List;

/**
 * 新闻表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface ArticleService extends IService<Article> {
     boolean insert(ArticleInsertDTO dto);
     PageVO<Article> page(ArticlePageDTO dto);
     // 获取最新的n条新闻
     List<Article> top(int n);
}
