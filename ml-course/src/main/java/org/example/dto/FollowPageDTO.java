package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-10 9:44
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "收藏分页DTO")
@Data
public class FollowPageDTO extends PageDTO {
    @Schema(description = "集次ID，集次表外键")
    private Long fkEpisodeId;
    @Schema(description = "用户ID，用户表外键")
    private Long fkUserId;
}
