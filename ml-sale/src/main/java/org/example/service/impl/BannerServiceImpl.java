package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Banner;
import org.example.mapper.BannerMapper;
import org.example.service.BannerService;
import org.springframework.stereotype.Service;

/**
 * 横幅表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner>  implements BannerService{

}
