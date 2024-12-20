package com.example.onlybuns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoadBalancerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test")
    public String testLoadBalancing() {
        // Umesto direktnog URL-a koristi naziv aplikacije
        String response = restTemplate.getForObject("http://ONLYBUNS/test-instance", String.class);
        return "Response from instance: " + response;
    }
}