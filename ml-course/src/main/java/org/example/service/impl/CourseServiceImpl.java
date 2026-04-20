package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Course;
import org.example.mapper.CourseMapper;
import org.example.service.CourseService;
import org.springframework.stereotype.Service;

/**
 * 课程表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>  implements CourseService{

}
