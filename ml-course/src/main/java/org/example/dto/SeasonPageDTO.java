package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-07 19:28
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "季次分页DTO")
@Data
public class SeasonPageDTO extends PageDTO {
    @Schema(description = "标题")
    private String title;
    @Schema(description = "课程ID，课程表外键")
    private Long fkCourseId;
}

