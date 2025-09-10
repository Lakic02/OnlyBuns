package com.example.onlybuns.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Fanout exchange koji već imate
    @Bean
    public Exchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange("postExchange").durable(true).build();
    }

    // Queue za primanje lokacija
    @Bean
    public Queue locationQueue() {
        return new Queue("location-queue", true); // durable
    }

    // Binding između queue i exchange
    @Bean
    public Binding locationBinding(Queue locationQueue, Exchange fanoutExchange) {
        return BindingBuilder.bind(locationQueue).to(fanoutExchange).with("").noargs();
    }
}
