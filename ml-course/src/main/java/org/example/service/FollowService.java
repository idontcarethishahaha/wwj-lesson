package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.FollowInsertDTO;
import org.example.dto.FollowPageDTO;
import org.example.entity.Follow;
import org.example.vo.PageVO;

/**
 * 收藏表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface FollowService extends IService<Follow> {
    boolean insert(FollowInsertDTO dto);
    PageVO<Follow> page(FollowPageDTO dto);
}
