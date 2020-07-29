package com.boss.springcloud.service.fallback;

import com.boss.springcloud.service.HelloService;
import org.springframework.stereotype.Component;

@Component
public class FallBack implements HelloService {
    @Override
    public String hello(String name) {

        String msg = "hello " + name + ", 出错了！！！";
        return msg;
    }
}
