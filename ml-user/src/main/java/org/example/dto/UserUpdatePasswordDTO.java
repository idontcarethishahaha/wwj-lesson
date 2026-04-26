package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.constant.ML;

import java.io.Serializable;

/*
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-26 9:34
 */
@Schema(name = "用户修改密码DTO")
@Data
public class UserUpdatePasswordDTO implements Serializable {

    @NotNull(message = "主键不能为空")
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "旧登录密码")
    @NotEmpty(message = "旧密码不能为空")
    @Pattern(regexp = ML.Regex.PASSWORD_RE, message = ML.Regex.PASSWORD_RE_MSG)
    private String oldPassword;

    @Schema(description = "新登录密码")
    @NotEmpty(message = "新密码不能为空")
    @Pattern(regexp = ML.Regex.PASSWORD_RE, message = ML.Regex.PASSWORD_RE_MSG)
    private String newPassword;
}

