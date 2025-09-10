package com.example.obcare.controller;

import com.example.obcare.model.LocationMessage;
import com.example.obcare.service.LocationSenderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "http://localhost:5174")
public class LocationController {

    private final LocationSenderService locationSenderService;

    public LocationController(LocationSenderService locationSenderService) {
        this.locationSenderService = locationSenderService;
    }

    @PostMapping
    public String sendLocation(@RequestBody LocationMessage location) {
        locationSenderService.sendLocation(location);
        return "Message sent!";
    }
}
