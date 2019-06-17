package com.tezamess.component;

import com.tezamess.model.ChatMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
        String group = (String) headerAccessor.getSessionAttributes().get("group");
        System.out.println("1111111111111111 --- ok");
        if (username != null && group != null) {
            System.out.println("User Disconnected : " + username);

            Map<String, Object> map = new HashMap<>();

            map.put("id", 0);
            map.put("createdate", new Date().getTime());
            map.put("body", "Offline");
            map.put("group", Integer.parseInt(group));
            map.put("user", Integer.parseInt(username));
            map.put("status", "Notify");
            map.put("type", "Offline");
           
            messagingTemplate.convertAndSend("/room/" + group, map);
        }
    }
}
