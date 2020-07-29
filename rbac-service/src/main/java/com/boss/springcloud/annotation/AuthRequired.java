package com.boss.springcloud.annotation;

import com.boss.springcloud.util.AuthCodeEnum;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthRequired {
    AuthCodeEnum value() default AuthCodeEnum.AUTHORITY_USER;
}
