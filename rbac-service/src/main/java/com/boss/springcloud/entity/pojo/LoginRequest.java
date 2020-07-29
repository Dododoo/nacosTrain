package com.boss.springcloud.entity.pojo;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;

    private String password;
}
