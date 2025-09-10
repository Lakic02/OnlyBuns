package com.example.onlybuns.service;

import com.example.onlybuns.domain.LocationMessage;
import com.example.onlybuns.repository.LocationMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class LocationListenerService {

    private final ObjectMapper objectMapper;
    private final LocationMessageRepository locationMessageRepository;


    public LocationListenerService(ObjectMapper objectMapper, LocationMessageRepository locationMessageRepository) {
        this.objectMapper = objectMapper;
        this.locationMessageRepository = locationMessageRepository;
    }

    @RabbitListener(queues = "care-queue")
    public void receiveMessage(String message) {
        try {
            // Konvertovanje JSON poruke u LocationMessage objekat
            LocationMessage location = objectMapper.readValue(message, LocationMessage.class);

            locationMessageRepository.save(location);

            // Ovde možeš sačuvati u bazu ili dodatno obraditi
            System.out.println("Received message: " + location.getName() +
                    " (" + location.getLatitude() + ", " + location.getLongitude() + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
