package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.CategoryInsertDTO;
import org.example.dto.CategoryPageDTO;
import org.example.dto.CategoryUpdateDTO;
import org.example.entity.Category;
import org.example.vo.CategorySimpleListVO;
import org.example.vo.PageVO;

import java.util.List;

/**
 * 课程类别表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface CategoryService extends IService<Category> {
    boolean insert(CategoryInsertDTO dto);
    List<CategorySimpleListVO> listSimple();
    PageVO<Category> page(CategoryPageDTO dto);

    boolean update(CategoryUpdateDTO dto);

    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
}
