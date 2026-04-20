package org.example.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.entity.Article;
import org.example.service.ArticleService;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 新闻表 控制层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@RestController
@Tag(name = "新闻表接口")
@RequestMapping("/api/v1/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 添加新闻表。
     *
     * @param article 新闻表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    @Operation(description="保存新闻表")
    public boolean save(@RequestBody @Parameter(description="新闻表")Article article) {
        return articleService.save(article);
    }

    /**
     * 根据主键删除新闻表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description="根据主键新闻表")
    public boolean remove(@PathVariable @Parameter(description="新闻表主键")Long id) {
        return articleService.removeById(id);
    }

    /**
     * 根据主键更新新闻表。
     *
     * @param article 新闻表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description="根据主键更新新闻表")
    public boolean update(@RequestBody @Parameter(description="新闻表主键")Article article) {
        return articleService.updateById(article);
    }

    /**
     * 查询所有新闻表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description="查询所有新闻表")
    public List<Article> list() {
        return articleService.list();
    }

    /**
     * 根据新闻表主键获取详细信息。
     *
     * @param id 新闻表主键
     * @return 新闻表详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description="根据主键获取新闻表")
    public Article getInfo(@PathVariable Long id) {
        return articleService.getById(id);
    }

    /**
     * 分页查询新闻表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description="分页查询新闻表")
    public Page<Article> page(@Parameter(description="分页信息")Page<Article> page) {
        return articleService.page(page);
    }

}
