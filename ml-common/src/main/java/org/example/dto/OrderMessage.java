package org.example.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-19 11:56
 */
@Data
public class OrderMessage implements Serializable {
    private Long fkUserId;
    private Long fkCourseId;
    private Double price;
    private Double skPrice;
}

