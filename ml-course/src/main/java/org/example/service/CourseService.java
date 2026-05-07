package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.CourseInsertDTO;
import org.example.dto.CoursePageDTO;
import org.example.entity.Course;
import org.example.vo.CourseSimpleListVO;
import org.example.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface CourseService extends IService<Course> {
    boolean insert(CourseInsertDTO dto);
    List<CourseSimpleListVO> simpleList();
    PageVO<Course> page(CoursePageDTO dto);

    // 上传课程封面
    String uploadCover(Long courseId, MultipartFile  coverFile);
}
