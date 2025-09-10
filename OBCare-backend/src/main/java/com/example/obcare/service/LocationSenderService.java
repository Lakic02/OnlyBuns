package com.example.obcare.service;

import com.example.obcare.model.LocationMessage;
import com.example.obcare.repository.LocationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocationSenderService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String exchange;
    private final String routingKey;
    private final LocationRepository locationRepository;

    public LocationSenderService(RabbitTemplate rabbitTemplate,
                                 ObjectMapper objectMapper,
                                 @Value("${app.rabbit.exchange}") String exchange,
                                 @Value("${app.rabbit.routing-key}") String routingKey,
                                 LocationRepository locationRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.locationRepository = locationRepository;
    }

    public void sendLocation(LocationMessage location) {
        try {
            // Sačuvaj u bazi i dobij automatski generisan ID
            LocationMessage savedLocation = locationRepository.save(location);

            // Pretvori u JSON i pošalji u RabbitMQ
            String message = objectMapper.writeValueAsString(savedLocation);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);

            System.out.println("Sent message: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
