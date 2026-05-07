package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.SeasonInsertDTO;
import org.example.dto.SeasonPageDTO;
import org.example.entity.Season;
import org.example.vo.PageVO;

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
}
