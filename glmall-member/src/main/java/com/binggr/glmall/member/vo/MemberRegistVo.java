package com.binggr.glmall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author: bing
 * @date: 2020/12/4 17:57
 */
@Data
public class MemberRegistVo {
    private String userName;

    private String passWord;

    private String phone;
}
