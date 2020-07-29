package com.boss.springcloud.entity.po;

import lombok.Data;

@Data
public class MenuPO {
    private int menuId;

    private String menuName;

    private String menuUrl;

    private int authorityId;
}
