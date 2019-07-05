package com.tezamess.component;

import com.tezamess.model.ResultModelV2;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.FriendRepositoryImpl;
import com.tezamess.repositoryimpl.UserRepositoryImpl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private FriendRepositoryImpl friendRepositoryImpl;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("ngat ket noi");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            int id = Integer.parseInt(username);
            Map<String, Object> map = new HashMap<>();

            map.put("createdate", new Date().getTime());
            map.put("body", "Offline");
            map.put("user", id);
            map.put("status", "Offline");
            map.put("type", "Notify");

            userRepositoryImpl.updateLastActive(id);

            UserModel user = userRepositoryImpl.findUserByIdWithRoom(id);
            user.getRoomModelList().stream().forEach(t -> {
                map.put("room", t.getId());
                messagingTemplate.convertAndSend("/room/" + t.getId(), map);
            });

            Map<String, Object> mapFriend = new HashMap<>();
            mapFriend.put("createdate", new Date().getTime());
            mapFriend.put("body", "Offline");
            mapFriend.put("user", id);
            mapFriend.put("type", "Notify");
            mapFriend.put("status", -1);
            List<UserModel> friends = friendRepositoryImpl.getFriends(Integer.parseInt(username));
            friends.stream().forEach(t -> {
                mapFriend.put("friend", t.getId());
                messagingTemplate.convertAndSend("/room/user/" + t.getId(), mapFriend);
            });
            System.out.println(" : User Disconnected : " + username);
        }
    }
}
