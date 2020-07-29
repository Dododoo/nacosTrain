package com.boss.springcloud.entity.po;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class UserPO {
    private String userId;

    private String userName;

    private String userPassword;

    private int userAge;

    private String userPhone;

    private int roleId;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private int status;
}
