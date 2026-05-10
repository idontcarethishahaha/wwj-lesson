package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-10 11:43
 */

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.constant.ML;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;


@Schema(name = "通知添加DTO")
@Data
public class NoticeInsertDTO implements Serializable {

    @Schema(description = "通知内容")
    @NotEmpty(message = "通知内容不能为空")
    @Pattern(regexp = ML.Regex.CONTENT_RE, message = ML.Regex.CONTENT_RE_MSG)
    private String content;

    @Schema(description = "序号")
    @NotNull(message = "序号不能为空")
    @Range(min = 0, message = "序号最小为0")
    private Long idx;
}
