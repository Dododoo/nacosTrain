package com.boss.springcloud.controller;

import com.boss.springcloud.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {
    @Autowired
    private HelloService helloService;

    @RequestMapping(path = "hello", method = RequestMethod.GET)
    public String hello(@RequestParam String name) {

        return helloService.hello(name);
    }


}
