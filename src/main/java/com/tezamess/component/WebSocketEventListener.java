/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tezamess.component;

import com.tezamess.model.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
/**
 *
 * @author user
 */
@Component
public class WebSocketEventListener {
 
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
 
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }
 
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
 
        String username = (String) headerAccessor.getSessionAttributes().get("username");
         
        if(username != null) {
            System.out.println("User Disconnected : " + username);
 
            HelloMessage chatMessage = new HelloMessage();
            chatMessage.setName(username);
            messagingTemplate.convertAndSend("/topic/publicChatRoom", chatMessage);
        }
    }
}
