package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Category;
import org.example.mapper.CategoryMapper;
import org.example.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * 课程类别表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>  implements CategoryService{

}
