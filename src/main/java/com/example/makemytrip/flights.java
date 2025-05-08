package com.example.makemytrip;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightsController {

    @GetMapping("/flights")
    public String getData() {
        return "Please book your hotels from MMT. Kindly book a ticket from New Delhi to anywhere at 20% discount.";
    }
}
