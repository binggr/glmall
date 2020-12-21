package com.binggr.glmall.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author: bing
 * @date: 2020/12/6 16:18
 */
@ToString
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;//一定封装
    private boolean tempUser = false;
}
