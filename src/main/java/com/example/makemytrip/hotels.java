package com.example.makemytrip;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HotelsController {

    @GetMapping("/hotels")
    public String getData() {
        return "Please book your hotels from MMT. Enjoy 20% off on bookings from New Delhi to anywhere.";
    }
}
