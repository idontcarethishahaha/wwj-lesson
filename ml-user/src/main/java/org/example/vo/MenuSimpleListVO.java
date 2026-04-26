package org.example.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
/*
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-26 14:45
 */
@Schema(name = "菜单全查VO")
@Data
public class MenuSimpleListVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "父菜单主键")
    private Long pid;
    @Schema(description = "父菜单标题")
    private String parentTitle;
}
