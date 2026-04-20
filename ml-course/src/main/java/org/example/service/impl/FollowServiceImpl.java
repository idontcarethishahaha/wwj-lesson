package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Follow;
import org.example.mapper.FollowMapper;
import org.example.service.FollowService;
import org.springframework.stereotype.Service;

/**
 * 收藏表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>  implements FollowService{

}
