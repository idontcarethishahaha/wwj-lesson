package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Report;
import org.example.mapper.ReportMapper;
import org.example.service.ReportService;
import org.springframework.stereotype.Service;

/**
 * 举报表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report>  implements ReportService{

}
