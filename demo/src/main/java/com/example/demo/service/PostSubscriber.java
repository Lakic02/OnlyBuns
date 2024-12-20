package main.java.com.example.demo.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class PostSubscriber {

    @RabbitListener(queues = "#{agencyQueue.name}")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
