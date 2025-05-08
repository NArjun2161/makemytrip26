package com.example.makemytrip;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BusController {

    @GetMapping("/bus")
    public String getData() {
        return "Testing: Microservice requirement fulfilled.";
    }
}
