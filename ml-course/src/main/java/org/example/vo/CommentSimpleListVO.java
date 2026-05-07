package org.example.vo;

/*
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-05-07 20:48
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(name = "评论全查VO")
@Data
public class CommentSimpleListVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "父评论ID")
    private Long pid;
    @Schema(description = "评论内容")
    private String content;
}
