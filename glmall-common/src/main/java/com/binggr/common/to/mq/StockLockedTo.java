package com.binggr.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author: bing
 * @date: 2020/12/10 16:05
 */
@Data
public class StockLockedTo {
    private Long id; //库存工作单id
    private StockDetailTo detail;//工作详情的所有id
}
