package com.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/")
public class UserController {

    @GetMapping("get")
    public String helloGet(){
        return "hello GET";
    }

    @GetMapping("getSecurity")
    public String helloGetS(){
        return "hello GET-SECURITY";
    }
}
