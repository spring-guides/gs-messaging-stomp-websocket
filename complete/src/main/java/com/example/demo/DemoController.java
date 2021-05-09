package com.example.demo;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.SendTo;

@Controller
public class DemoController {

    @MessageMapping("/hello")
    @SendTo("/messages")
    public Message sendMessage(Message message) {
        return message;
    }

}
