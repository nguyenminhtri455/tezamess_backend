package com.tezamess.controller;

import com.tezamess.map.MappedRoomModel;
import com.tezamess.model.ChatMessage;
import com.tezamess.model.MessageModel;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.serviceimpl.MessageServiceImpl;
import com.tezamess.serviceimpl.RoomServiceImpl;
import java.util.Date;
import java.util.Set;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    @Autowired
    private RoomServiceImpl roomServiceImpl;

    @Autowired
    private MessageServiceImpl messageServiceImpl;

    @Autowired
    private SimpMessageSendingOperations sendingOperations;

    //tao phong chat
    @MessageMapping("/room.create")
    public void createRoom(@Payload String json) {
        System.out.println(json);

        RoomModel room = roomServiceImpl.findOrCreateRoom(json);
        if (room != null) {
            Set<UserModel> listUser = room.getUserModelList();
            listUser.stream().forEach(t -> {
                System.out.println(t.getId() + "----id nhan duoc thong bao tao room");
                sendingOperations.convertAndSend("/room/user/" + t.getId(),
                        new ResultModelV2(ResultModelV2.Status.CREATE_ROOM.getStatus(),
                                MappedRoomModel.convertToMap(room),
                                ResultModelV2.Status.CREATE_ROOM.name(),
                                new Date()));
            });
        } else {
            System.out.println("loi !!!!!");
        }

    }

    //gui tin nhan den phong chat
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/room/{roomId}")
    public String sendMessage(@Payload String message, @DestinationVariable String roomId) {
        System.out.println(message);
//        messageServiceImpl.saveMessage(message);
        return message;
    }

    //gui yeu cau ket ban
    @MessageMapping("/addfriend")
    public void addfriend(@Payload String json) {
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("idRequest");
        int idfriend = jSONObject.getInt("idFriend");
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.ADD_FRIEND_REQUEST.getStatus(),
                        new JSONObject(json).toString(),
                        ResultModelV2.Status.ADD_FRIEND_REQUEST.name(),
                        new Date()));

        sendingOperations.convertAndSend("/room/user/" + idfriend,
                new ResultModelV2(ResultModelV2.Status.ADD_FRIEND_REQUEST.getStatus(),
                        new JSONObject(json).toString(),
                        ResultModelV2.Status.ADD_FRIEND_REQUEST.name(),
                        new Date()));
    }

    @MessageMapping("/notify.user/{userId}")
    @SendTo("/room/user/{userId}")
    public String receiveNotifications(@Payload String json, @DestinationVariable String userId) {
        System.out.println(json);
        return null;
    }

    @MessageMapping("/chat.joinRoom/{roomId}")
    @SendTo("/room/{roomId}")
    public ChatMessage joinRoom(@Payload ChatMessage chatMessage, @DestinationVariable String roomId,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session     
        System.out.println(chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.leaveRoom/{roomId}")
    @SendTo("/room/{roomId}")
    public ChatMessage leaveRoom(@Payload ChatMessage chatMessage, @DestinationVariable String roomId,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session

        System.out.println(chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    //test by Android
    @MessageMapping("/hello")
    @SendTo("/room/greetings")
    public String greeting(@Payload String name) {
        System.out.println(name);
        return "Hello, " + name + "!";
    }

    //test by browser
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        System.out.println(chatMessage.getContent());
        System.out.println("TTTTTTTTTTTTt  " + greeting("ok"));
        return chatMessage;
    }

    //test by browser
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        System.out.println(chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        System.out.println(headerAccessor.getSessionId() + " id");
        return chatMessage;
    }
}
