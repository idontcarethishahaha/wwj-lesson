package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.NoticeInsertDTO;
import org.example.dto.NoticePageDTO;
import org.example.entity.Notice;
import org.example.vo.PageVO;

import java.util.List;

/**
 * 通知表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface NoticeService extends IService<Notice> {
    boolean insert(NoticeInsertDTO dto);
    PageVO<Notice> page(NoticePageDTO dto);
    // 获取最新的n条通知
    List<Notice> top(Long n);
}
