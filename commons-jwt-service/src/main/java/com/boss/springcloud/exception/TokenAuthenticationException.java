package com.boss.springcloud.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenAuthenticationException extends Exception {
    private int code;
    private String message;

    public TokenAuthenticationException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
