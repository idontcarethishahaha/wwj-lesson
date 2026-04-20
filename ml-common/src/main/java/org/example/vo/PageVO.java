package org.example.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 类说明：
 *
 * @author WuWenJin
 * @version 1.0
 * @date 2026-04-19 11:53
 */
@ToString(callSuper = true)
@Data
public class PageVO<T> implements Serializable {
    private Long pageNum;
    private Integer pageSize;
    private Long totalRow;
    private Integer totalPage;
    private List<T> records;
}
