package com.binggr.glmall.member.exception;

/**
 * @author: bing
 * @date: 2020/12/4 18:47
 */
public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("手机号存在");
    }
}
