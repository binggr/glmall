package com.binggr.glmall.member.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: bing
 * @date: 2020/12/4 20:56
 */
@Data
public class MemberLoginVo implements Serializable {
    private String loginacct;
    private String password;
}
