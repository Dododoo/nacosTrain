package com.boss.springcloud.service;

import com.boss.springcloud.service.fallback.FallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "nacos-provider", fallback = FallBack.class)
public interface HelloService {

    @RequestMapping(path = "/hello/nacos")
    public String hello(@RequestParam String name);
}
