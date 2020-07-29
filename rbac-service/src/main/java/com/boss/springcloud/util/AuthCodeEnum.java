package com.boss.springcloud.util;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum AuthCodeEnum {

    AUTHORITY_SYSTEM(0, "拥有系统权限"),
    AUTHORITY_USER(2, "普通用户权限"),
    AUTHORITY_FINANCE(1, "财务管理人员权限");
    //Allow(0);

    private int authority_id;
    private String desc;

    AuthCodeEnum(int authority_id, String desc) {
        this.authority_id =authority_id;
        this.desc = desc;
    }

}
