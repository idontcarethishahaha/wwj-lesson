package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Episode;
import org.example.mapper.EpisodeMapper;
import org.example.service.EpisodeService;
import org.springframework.stereotype.Service;

/**
 * 集次表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class EpisodeServiceImpl extends ServiceImpl<EpisodeMapper, Episode>  implements EpisodeService{

}
