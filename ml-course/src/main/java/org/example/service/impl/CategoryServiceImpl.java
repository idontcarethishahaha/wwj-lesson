package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.dto.CategoryInsertDTO;
import org.example.dto.CategoryPageDTO;
import org.example.dto.CategoryUpdateDTO;
import org.example.entity.Category;
import org.example.exception.ServiceException;
import org.example.mapper.CategoryMapper;
import org.example.result.ResultCode;
import org.example.service.CategoryService;
import org.example.vo.CategorySimpleListVO;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.example.entity.table.CategoryTableDef.CATEGORY;

/**
 * 课程类别表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>  implements CategoryService{
    // ctrl+i
    @Override
    public boolean insert(CategoryInsertDTO dto) {
        String title = dto.getTitle();
        // 判断标题是否重复
        if(QueryChain.of(mapper)
            .where(CATEGORY.TITLE.eq(title))
            .exists()){
            throw new ServiceException(ResultCode.TITLE_REPEAT,"该标题已存在，请勿重复添加");
        }
        // 复制dto中的属性
        Category category = BeanUtil.copyProperties(dto,Category.class);
        if(StrUtil.isBlank(dto.getInfo())){
            category.setInfo("暂无描述");
        }
        // 设置创建时间和修改时间
        category.setCreated(LocalDateTime.now());
        category.setUpdated(LocalDateTime.now());
        if(mapper.insert(category)==0){
            throw new ServiceException(ResultCode.MYSQL_ERROR,"数据操作失败");
        }
        return true;
    }

    @Override
    public List<CategorySimpleListVO> listSimple() {
        List<CategorySimpleListVO> list = QueryChain.of(mapper)
            .select(CATEGORY.ID,CATEGORY.TITLE)
            .orderBy(CATEGORY.IDX.asc(),CATEGORY.ID.desc())
            .listAs(CategorySimpleListVO.class);
        return list;
    }

    @Override
    public PageVO<Category> page(CategoryPageDTO dto) {
        String title = dto.getTitle();
        QueryChain<Category> queryChain = QueryChain.of(mapper)
            .orderBy(CATEGORY.IDX.asc(),CATEGORY.ID.desc());
        if(StrUtil.isNotBlank(title)){
            // 模糊查询
            queryChain.where(CATEGORY.TITLE.like(title));
        }
        // 执行分页查询
        Page<Category> result
                = queryChain.page(new Page<>(dto.getPageNum(),dto.getPageSize()));
        // 将查询结果转换成项目自定义的PageVO
        PageVO<Category> pageVO=new PageVO<>();
        BeanUtil.copyProperties(result,pageVO);
        pageVO.setPageNum(result.getPageNumber());
        return pageVO;
    }

    @Override
    public boolean update(CategoryUpdateDTO dto) {
        String title = dto.getTitle();
        Long id = dto.getId();

        // 检查课程类别是否存在
        this.existsById(id);

        // 标题查重
        // select count(1) from category where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(CATEGORY.TITLE.eq(title))
                .and(CATEGORY.ID.ne(id))
                .exists()) {
            throw new ServiceException(ResultCode.TITLE_REPEAT, "标题" + title + "重复");
        }

        // 组装实体类
        Category category = BeanUtil.copyProperties(dto, Category.class);
        category.setUpdated(LocalDateTime.now());

        // update category set title = ?, info = ?, idx = ?, updated = ? where id = ?
        if (!UpdateChain.of(category)
                .where(CATEGORY.ID.eq(category.getId()))
                .update()) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库修改失败");
        }
        return true;
    }

    /**
     * 按主键检查课程类别是否存在，如果不存在则直接抛出异常
     *
     * @param id 课程类别主键
     */
    private void existsById(Long id) {
        // select count(*) from category where id = ?
        if (!QueryChain.of(mapper)
                .where(CATEGORY.ID.eq(id))
                .exists()) {
            throw new ServiceException(ResultCode.CATEGORY_NOT_FOUND, id + "号课程类别数据不存在");
        }
    }

    @Override
    public boolean delete(Long id) {

        // 检查课程类别是否存在
        this.existsById(id);

        // delete from category where id = ?
        if (mapper.deleteById(id) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
        return true;
    }

    @Override
    public boolean deleteBatch(List<Long> ids) {

        // 检查课程类别是否存在
        // select count(*) from category where id in (?, ?, ?)
        if (QueryChain.of(mapper)
                .where(CATEGORY.ID.in(ids))
                .count() < ids.size()) {
            throw new ServiceException(ResultCode.CATEGORY_NOT_FOUND, "至少一个课程类别数据不存在");
        }

        // delete from category where id in (?, ?, ?)
        if (mapper.deleteBatchByIds(ids) <= 0) {
            throw new ServiceException(ResultCode.MYSQL_ERROR, "数据库删除失败");
        }
        return true;
    }
}
