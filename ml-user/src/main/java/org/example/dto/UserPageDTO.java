package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-23 20:36
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "用户分页DTO")
@Data
public class UserPageDTO extends PageDTO {
    @Schema(description = "登录账号")
    private String username;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "手机号码")
    private String phone;
}
