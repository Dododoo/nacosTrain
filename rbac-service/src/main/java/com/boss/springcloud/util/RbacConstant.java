package com.boss.springcloud.util;

public interface RbacConstant {
    /**
     *系统权限
     * */
    int SYSTEM_AUTHORIITY = 0;

    /**
     * 普通用户权限
     * */
    int USER_AUTHORITY = 1;

    /**
     * 财务人员权限
     * */
    int FINANCE_AUTHORITY = 2;

    /**
     * 有效用户
     * */
    int EFFECTIVE_USER = 1;

    /**
     * 无效用户
     * */
    int INVALID_USER = -1;

}
