package com.example.makemytrip;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Welcome {
    @GetMapping("Welcome")
    public String getData() {return  "WELCOME TO DEVOPS....||" ; }
}
