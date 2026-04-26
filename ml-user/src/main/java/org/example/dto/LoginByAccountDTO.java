package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-26 10:49
 */

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.constant.ML;

import java.io.Serializable;

@Schema(name = "用户登录DTO", description = "通过账号密码登录")
@Data
public class LoginByAccountDTO implements Serializable {

    @NotEmpty(message = "登录账号不能为空")
    @Pattern(regexp = ML.Regex.USERNAME_RE, message = ML.Regex.USERNAME_RE_MSG)
    @Schema(description = "登录账号")
    private String username;

    @NotEmpty(message = "登录密码不能为空")
    @Pattern(regexp = ML.Regex.PASSWORD_RE, message = ML.Regex.PASSWORD_RE_MSG)
    @Schema(description = "登录密码")
    private String password;
}
