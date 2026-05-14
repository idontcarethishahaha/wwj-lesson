package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-14 19:27
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "购物车分页DTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartPageDTO extends PageDTO {
    @Schema(description = "用户账号，冗余字段")
    private String username;
    @Schema(description = "课程标题，冗余字段")
    private String courseTitle;

    @Schema(description = "课程id，冗余字段")
    private Long fkCourseId;

    @Schema(description = "用户id，冗余字段")
    private Long fkUserId;
}
