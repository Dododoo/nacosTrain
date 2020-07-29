package com.boss.springcloud.entity.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class UserDTO {
    private String userName;

    private String userPassword;

    private int userAge;

    private String userPhone;

    private String roleName;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private int authorityId;

    private int status;

    //private List<MenuPO> menuList;
}
