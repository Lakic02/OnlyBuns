package com.example.onlybuns.controller;

import com.example.onlybuns.domain.LocationMessage;
import com.example.onlybuns.repository.LocationMessageRepository;
import com.example.onlybuns.service.LocationListenerService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LocationMessageController {

    private final LocationMessageRepository locationMessageRepository;
    private final LocationListenerService locationListenerService;

    public LocationMessageController(LocationMessageRepository locationMessageRepository, LocationListenerService locationListenerService) {
        this.locationMessageRepository = locationMessageRepository;
        this.locationListenerService = locationListenerService;
    }

    @GetMapping("/api/messages")
    public List<LocationMessage> getAllMessages() {
        locationListenerService.receiveMessage("Test message from controller");
        return locationMessageRepository.findAll();
    }
}
