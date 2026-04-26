package org.example.vo;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-26 10:48
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.entity.Menu;
import org.example.entity.User;

import java.io.Serializable;
import java.util.List;

@Schema(name = "用户登录VO")
@Data
public class LoginVO implements Serializable {
    @Schema(description = "用户信息")
    private User user;
    @Schema(description = "Token令牌")
    private String token;
    @Schema(description = "角色列表")
    private List<String> roleTitles;
    @Schema(description = "菜单列表")
    private List<Menu> menus;
}
