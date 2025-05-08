package com.example.makemytrip;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrainController {

    @GetMapping("/train")
    public String getData() {
        return "Please book your train tickets from MMT. Enjoy 20% off on trips from New Delhi to any destination.";
    }
}
