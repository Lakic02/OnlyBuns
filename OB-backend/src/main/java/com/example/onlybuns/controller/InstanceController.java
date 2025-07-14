package com.example.onlybuns.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceController {

    @Value("${server.port}")
    private String port;

    @GetMapping("/test-instance")
    public String getInstanceInfo() {
        return "Instance running on port: " + port;
    }
}