package com.binggr.common.to.mq;

import lombok.Data;

/**
 * @author: bing
 * @date: 2020/12/10 16:17
 */
@Data
public class StockDetailTo {
    private Long Id;

    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;

    /**
     * 仓库Id
     */
    private Long wareId;

    /**
     * 锁定状态
     */
    private Integer lockStatus;
}
