package com.binggr.glmall.member.exception;

/**
 * @author: bing
 * @date: 2020/12/4 18:46
 */
public class UsernameExistException extends RuntimeException {
    public UsernameExistException() {
        super("用户名存在");
    }
}
