package com.example.msstore.rest.services;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String queueName,Object message){
        this.rabbitTemplate.convertAndSend(queueName,message);
    }
}
