package com.tezamess.component;

import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.UserRepositoryImpl;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {

            Map<String, Object> map = new HashMap<>();

            map.put("createdate", new Date().getTime());
            map.put("body", "Offline");
            map.put("user", Integer.parseInt(username));
            map.put("status", "Offline");
            map.put("type", "Notify");

            UserModel user = userRepositoryImpl.findUserByIdWithRoom(Integer.parseInt(username));
            user.getRoomModelList().stream().forEach(t -> {
                System.out.println(t.getId() + " : User Disconnected : " + username);
                map.put("room", t.getId());
                messagingTemplate.convertAndSend("/room/" + t.getId(), map);
            });

        }
    }
}
