package com.boss.springcloud.entity.vo;

import lombok.Data;

@Data
public class MenuVO {
    private int menuId;

    private String menuName;

    private String menuUrl;

    private int authorityId;
}
