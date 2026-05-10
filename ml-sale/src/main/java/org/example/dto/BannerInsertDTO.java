package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-10 14:43
 */

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.constant.ML;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

@Schema(name = "横幅添加DTO")
@Data
public class BannerInsertDTO implements Serializable {

    @Schema(description = "序号")
    @NotNull(message = "序号不能为空")
    @Range(min = 0, message = "序号最小为0")
    private Long idx;

    @Schema(description = "描述")
    @Pattern(regexp = ML.Regex.INFO_RE, message = ML.Regex.INFO_RE_MSG)
    private String info;
}

