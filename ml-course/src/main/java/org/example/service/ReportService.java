package org.example.service;

import com.mybatisflex.core.service.IService;
import org.example.dto.ReportInsertDTO;
import org.example.dto.ReportPageDTO;
import org.example.entity.Report;
import org.example.vo.PageVO;

/**
 * 举报表 服务层。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
public interface ReportService extends IService<Report> {
   boolean insert(ReportInsertDTO insertDTO);
   PageVO<Report> page(ReportPageDTO pageDTO);
}
