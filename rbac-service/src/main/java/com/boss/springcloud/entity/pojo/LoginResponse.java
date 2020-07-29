package com.boss.springcloud.entity.pojo;

import lombok.Data;

@Data
public class LoginResponse {
    private String username;

    private String token;

    private String refreshToken;
}
