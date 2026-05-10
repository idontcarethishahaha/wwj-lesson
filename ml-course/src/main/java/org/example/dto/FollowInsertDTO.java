package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-10 9:43
 */

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;


@Schema(name = "收藏添加DTO")
@Data
public class FollowInsertDTO implements Serializable {

    @Schema(description = "集次表ID，集次表外键")
    @NotNull(message = "集次表ID不能为空")
    private Long fkEpisodeId;

    @Schema(description = "用户表ID，用户表外键")
    @NotNull(message = "用户表ID不能为空")
    private Long fkUserId;
}

