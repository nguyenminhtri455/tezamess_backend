
package com.tezamess.controller;

import com.tezamess.model.Greeting;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    
    @MessageMapping("/ws")
    @SendTo("/topic/greetings")
    public Greeting greeting(String message) throws Exception {
        System.out.println("123");
        System.out.println(message);
        return new Greeting("Hello, " + message + "!");
        
       
    }
}
