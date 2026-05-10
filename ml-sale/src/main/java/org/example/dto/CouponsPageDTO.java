package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-10 15:45
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "优惠卷分页DTO")
@Data
public class CouponsPageDTO extends PageDTO {
    @Schema(description = "标题")
    private String title;
    @Schema(description = "口令")
    private String code;
}

