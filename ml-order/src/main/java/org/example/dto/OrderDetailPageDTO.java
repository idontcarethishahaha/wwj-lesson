package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-14 20:31
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "订单明细分页DTO")
@Data
public class OrderDetailPageDTO extends PageDTO {
    @Schema(description = "课程标题")
    private String courseTitle;
}

