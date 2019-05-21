
package com.tezamess.controller;

import com.tezamess.model.Greeting;
import com.tezamess.model.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        System.out.println(message.getName());
        return new Greeting("Hello, " + message.getName() + "!");
    }
}
