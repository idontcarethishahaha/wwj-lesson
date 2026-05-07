package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.dto.CategoryInsertDTO;
import org.example.dto.CategoryPageDTO;
import org.example.entity.Category;
import org.example.exception.ServiceException;
import org.example.mapper.CategoryMapper;
import org.example.result.ResultCode;
import org.example.service.CategoryService;
import org.example.vo.CategorySimpleListVO;
import org.example.vo.PageVO;
import org.springframework.stereotype.Service;

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
}
