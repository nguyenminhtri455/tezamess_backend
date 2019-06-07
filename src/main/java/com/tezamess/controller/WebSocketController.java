package com.tezamess.controller;

import com.tezamess.model.ChatMessage;
import com.tezamess.serviceimpl.RoomServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    @Autowired
    private RoomServiceImpl roomServiceImpl;

    @MessageMapping("/hello")
    @SendTo("/room/greetings")
    public String greeting(@Payload String name) {
        System.out.println("test-------------");
        System.out.println(name);
        return "Hello, " + name + "!";
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        System.out.println(chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        System.out.println(chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

//    @MessageMapping("/chat.sendMessage/hello")   
//    @SendTo("/room/hello")
//    public String sendMessage(String chatMessage) {
////        System.out.println(roomId);
//        System.out.println(chatMessage);
//        return chatMessage;
//    }
//
//    @MessageMapping("/chat.joinRoom/{roomId}")
//    @SendTo("/room/{roomId}")
//    public ChatMessage joinRoom(@Payload ChatMessage chatMessage, @DestinationVariable String roomId,
//            SimpMessageHeaderAccessor headerAccessor) {
//        // Add username in web socket session     
//        System.out.println(chatMessage.getSender());
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        return chatMessage;
//    }
//
//    @MessageMapping("/chat.leaveRoom/{roomId}")
//    @SendTo("/room/{roomId}")
//    public ChatMessage leaveRoom(@Payload ChatMessage chatMessage, @DestinationVariable String roomId,
//            SimpMessageHeaderAccessor headerAccessor) {
//        // Add username in web socket session
//
//        System.out.println(chatMessage.getSender());
//        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
//        return chatMessage;
//    }
}
