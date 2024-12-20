package main.java.com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Autowired
    private FanoutExchange fanoutExchange;

    @Bean
    public Queue agencyQueue() {
        return new AnonymousQueue(); 
    }

    @Bean
    public Binding binding(Queue agencyQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(agencyQueue).to(fanoutExchange);
    }
}
