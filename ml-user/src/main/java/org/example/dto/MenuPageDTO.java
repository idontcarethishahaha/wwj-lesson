package org.example.dto;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-26 14:44
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "菜单分页DTO")
@Data
public class MenuPageDTO extends PageDTO {
    @Range(min = 0, message = "父菜单主键最小为0")
    @Schema(description = "父菜单主键，0视为根节点")
    private Long pid;
    @Schema(description = "标题")
    private String title;
}
