package com.example.msuser.rabbitmq;

import com.example.msuser.rest.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Consumer {

    private final UserService userService;

    @RabbitListener(queues = Constants.QUEUE_DELETE_USER)
    public void deleteOwner(Long ownerId){
        userService.deleteOwnerById(ownerId);
    }
}
