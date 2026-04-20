package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Season;
import org.example.mapper.SeasonMapper;
import org.example.service.SeasonService;
import org.springframework.stereotype.Service;

/**
 * 季次表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class SeasonServiceImpl extends ServiceImpl<SeasonMapper, Season>  implements SeasonService{

}
