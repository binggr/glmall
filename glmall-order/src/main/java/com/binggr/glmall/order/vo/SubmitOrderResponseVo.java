package com.binggr.glmall.order.vo;

import com.binggr.glmall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author: bing
 * @date: 2020/12/8 19:11
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;//0成功，错误状态码
}
