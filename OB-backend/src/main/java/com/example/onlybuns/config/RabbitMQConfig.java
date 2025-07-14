package com.example.onlybuns.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Exchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange("postExchange").durable(true).build();
    }
}
