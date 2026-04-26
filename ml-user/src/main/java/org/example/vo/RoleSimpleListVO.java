package org.example.vo;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-26 11:33
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(name = "角色全查VO")
@Data
public class RoleSimpleListVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "标题")
    private String title;
}

