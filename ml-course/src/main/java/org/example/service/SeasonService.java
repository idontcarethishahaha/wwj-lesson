package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.SeasonInsertDTO;
import org.example.dto.SeasonPageDTO;
import org.example.entity.Season;
import org.example.vo.PageVO;

import java.util.List;

/**
 * 季次表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface SeasonService extends IService<Season> {
    boolean insert(SeasonInsertDTO dto);
    PageVO<Season> page(SeasonPageDTO dto);
    Season getById(Long id);

    // 获取课程下的所有季次
    List<Season> list(Long courseId);

}
