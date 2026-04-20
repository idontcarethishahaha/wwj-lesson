package org.example.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.example.entity.Menu;
import org.example.mapper.MenuMapper;
import org.example.service.MenuService;
import org.springframework.stereotype.Service;

/**
 * 菜单表 服务层实现。
 *
 * @author WuWenJin
 * @since v1.0.0
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>  implements MenuService{

}
