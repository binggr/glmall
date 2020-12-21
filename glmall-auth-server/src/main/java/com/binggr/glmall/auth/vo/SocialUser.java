package com.binggr.glmall.auth.vo;

/**
 * @author: bing
 * @date: 2020/12/5 14:31
 */
/**
 * Copyright 2020 bejson.com
 */

import lombok.Data;

/**
 * Auto-generated: 2020-12-05 14:30:21
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SocialUser {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}