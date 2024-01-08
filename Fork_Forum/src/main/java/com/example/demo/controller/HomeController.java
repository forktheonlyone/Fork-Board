package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 메인 홈페이지
    @GetMapping(value = {"/", ""})
    public String home(){
        return "index";
    }
}