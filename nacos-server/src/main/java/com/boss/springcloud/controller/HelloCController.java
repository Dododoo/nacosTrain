package com.boss.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloCController {

    @Value("${user.name}")
    private String name;

    @Value("${user.age}")
    private int age;

    @RequestMapping(path = "/hello/nacos", method = RequestMethod.GET)
    public String hello(@RequestParam("name") String name) {
        String msg = "配置中心的name:" + name + ", 年龄为:" + age;
        return "hi: " + name + " " + msg;
    }
}
