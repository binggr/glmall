package com.binggr.glmall.auth.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: bing
 * @date: 2020/12/4 20:43
 */
@Data
public class UserLoginVo implements Serializable {
    private String loginacct;
    private String password;
}
