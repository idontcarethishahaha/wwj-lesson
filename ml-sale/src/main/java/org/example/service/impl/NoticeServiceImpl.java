package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Notice;
import org.example.mapper.NoticeMapper;
import org.example.service.NoticeService;
import org.springframework.stereotype.Service;

/**
 * 通知表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>  implements NoticeService{

}
