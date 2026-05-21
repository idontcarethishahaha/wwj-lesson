package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.component.MyRedis;
import org.example.constant.ML;
import org.example.dto.ArticleInsertDTO;
import org.example.dto.ArticlePageDTO;
import org.example.entity.Article;
import org.example.exception.ServiceException;
import org.example.mapper.ArticleMapper;
import org.example.result.ResultCode;
import org.example.service.ArticleService;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.entity.table.ArticleTableDef.ARTICLE;

/**
 * 新闻表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>  implements ArticleService{
    @Resource
    private MyRedis myRedis;
    @Override
    public boolean insert(ArticleInsertDTO dto) {
        // 获取 title
        String title = dto.getTitle();
        // 判断数据表中是否已存在这个title
        if (QueryChain.of(mapper)
                .where(ARTICLE.TITLE.eq(title))
                .exists()){
            throw new ServiceException(ResultCode.TITLE_REPEAT,"该标题已存在，请勿重复添加");
        }
        // 复制属性
        Article article = BeanUtil.copyProperties(dto, Article.class);
        // 设置创建时间和修改时间
        article.setCreated(LocalDateTime.now());
        article.setUpdated(LocalDateTime.now());
        if (mapper.insert(article) == 0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"添加数据库失败");
        }
        // 添加完新的新闻时，将所有信息缓存删除
        myRedis.deleteByPrefix(ML.Redis.TOP_ARTICLE_KEY_PREFIX);
        return true;
    }

    @Override
    public PageVO<Article> page(ArticlePageDTO dto) {
        QueryChain queryChain = QueryChain.of(mapper)
                .orderBy(ARTICLE.IDX.asc(),ARTICLE.ID.desc());
        // 获取title
        String title = dto.getTitle();
        if (StrUtil.isNotBlank(title)){
            queryChain.where(ARTICLE.TITLE.like(title));
        }
        // 分页查询转VO
        Page<Article> result = queryChain.page(new Page(dto.getPageNum(),dto.getPageSize()));
        PageVO<Article> pageVO = new PageVO<>();
        pageVO.setPageNum(result.getPageNumber());
        BeanUtil.copyProperties(result,pageVO);
        return pageVO;
    }

    @Override
    public List<Article> top(int n) {
        // 获取redis缓存键
        String redisKey = ML.Redis.TOP_ARTICLE_KEY_PREFIX + n;
        // 如果Redis中存在，则直接返回
        if (myRedis.exists(redisKey)){
            return JSONUtil.toList(myRedis.get(redisKey),Article.class);
        }
        // 如果Redis中不存在，则从数据库中查询
        List<Article> articles = QueryChain.of(mapper)
                .orderBy(ARTICLE.IDX.asc(),ARTICLE.ID.desc())
                .limit(n)
                .select()//可以不写
                .list();
        // 缓存到Redis中
        myRedis.setEx(redisKey,JSONUtil.toJsonStr(articles),3, TimeUnit.HOURS);//3小时过期
        // 返回结果
        return articles;
    }
}
