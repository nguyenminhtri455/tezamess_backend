package com.tezamess.controller;

import com.tezamess.map.MappedRoomModel;
import com.tezamess.map.MappedUserModel;
import com.tezamess.model.ChatMessage;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.serviceimpl.FriendServiceImpl;
import com.tezamess.serviceimpl.MessageServiceImpl;
import com.tezamess.serviceimpl.RoomServiceImpl;
import com.tezamess.serviceimpl.UserServiceImpl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    @Autowired
    private RoomServiceImpl roomServiceImpl;

    @Autowired
    private MessageServiceImpl messageServiceImpl;

    @Autowired
    private FriendServiceImpl friendServiceImpl;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private SimpMessagingTemplate sendingOperations;

    //tim hoac tao phong chat
    @MessageMapping("/room.create")
    public void createRoom(@Payload String json) {
        System.out.println(json);
        roomServiceImpl.findOrCreateRoom(json);
    }

    //client phan hoi da nhan duoc phong chat (khong con su dung)
    @MessageMapping("/response/receivedroom")
    public void responseReceivedRoom(@Payload String json) {
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("id");
        int idRoom = jSONObject.getInt("idRoom");
        roomServiceImpl.changeStatusReceivedRoom(id, idRoom);

    }

    //tim phong chat
    @MessageMapping("/room.find")
    public void findRoom(@Payload String json) {
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("id");
        int idRoom = jSONObject.getInt("idRoom");
        RoomModel room = roomServiceImpl.findRoom(idRoom);
        if (room != null) {
            sendingOperations.convertAndSend("/room/user/" + id,
                    new ResultModelV2(ResultModelV2.Status.FIND_ROOM.getStatus(),
                            MappedRoomModel.convertToMap(room),
                            ResultModelV2.Status.FIND_ROOM.name(),
                            new Date()));
        } else {
            System.out.println("loi !!!!!");
        }
    }

    //gui tin nhan den phong chat
    @MessageMapping("/chat.sendMessage/{roomId}")
//    @SendTo("/room/{roomId}")
    public void sendMessage(@Payload String message, @DestinationVariable String roomId) {
        System.out.println(message);
        messageServiceImpl.saveMessage(message);
    }

    //gui phan hoi trang thai tin nhan den phong chat
    @MessageMapping("/chat.sendMessageResponse/{roomId}")
    @SendTo("/room/{roomId}")
    public String sendMessageResponse(@Payload String message, @DestinationVariable String roomId) {
        System.out.println(message + " /chat.sendMessageResponse/" + roomId);
        return messageServiceImpl.updateStatusMessage(message);
    }

    //gui yeu cau ket ban
    @MessageMapping("/addfriend")
    public void addfriend(@Payload String json) {
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("idRequest");
        int idfriend = jSONObject.getInt("idFriend");
        friendServiceImpl.requestAddFriend(id, idfriend);
        UserModel userRequest = userServiceImpl.getUserById(id);
        UserModel userFriend = userServiceImpl.getUserById(idfriend);
        List<Map<String, Object>> convertToListBy5Record = MappedUserModel.convertToListBy5Record(userRequest, userFriend);
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.ADD_FRIEND_REQUEST.getStatus(),
                        convertToListBy5Record,
                        ResultModelV2.Status.ADD_FRIEND_REQUEST.name(),
                        new Date()));

        sendingOperations.convertAndSend("/room/user/" + idfriend,
                new ResultModelV2(ResultModelV2.Status.ADD_FRIEND_REQUEST.getStatus(),
                        convertToListBy5Record,
                        ResultModelV2.Status.ADD_FRIEND_REQUEST.name(),
                        new Date()));
    }

    //gui yeu cau ket ban
    @MessageMapping("/unfriend")
    public void unFriend(@Payload String json) {
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("idRequest");
        int idfriend = jSONObject.getInt("idFriend");
        friendServiceImpl.unFriend(id, idfriend);
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.UNFRIEND.getStatus(),
                        json,
                        ResultModelV2.Status.UNFRIEND.name(),
                        new Date()));

        sendingOperations.convertAndSend("/room/user/" + idfriend,
                new ResultModelV2(ResultModelV2.Status.UNFRIEND.getStatus(),
                        json,
                        ResultModelV2.Status.UNFRIEND.name(),
                        new Date()));
    }

    //phan hoi yeu cau ket ban
    @MessageMapping("/response/addfriend")
    public void responseAddfriend(@Payload String json) {
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("idRequest");
        int idfriend = jSONObject.getInt("idFriend");
        int status = jSONObject.getInt("status");
        friendServiceImpl.addFriend(id, idfriend, status);
        ResultModelV2 resultModelV2;
        if (status == -1) {
            resultModelV2 = new ResultModelV2(ResultModelV2.Status.DISAGREE_ADDFRIEND.getStatus(),
                    json,
                    ResultModelV2.Status.DISAGREE_ADDFRIEND.name(),
                    new Date());
        } else {
            resultModelV2 = new ResultModelV2(ResultModelV2.Status.AGREE_ADDFRIEND.getStatus(),
                    json,
                    ResultModelV2.Status.AGREE_ADDFRIEND.name(),
                    new Date());
        }
        sendingOperations.convertAndSend("/room/user/" + id, resultModelV2);
        sendingOperations.convertAndSend("/room/user/" + idfriend, resultModelV2);
    }

    //phan hoi da nhan phan hoi ket ban
    @MessageMapping("/response/receivedaddfriend")
    public void responseAgreeAddfriend(@Payload String json) {
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("idRequest");
        int status = jSONObject.getInt("status");
        // xoa nhung record co trang thai la 1 cua nguoi gui
        friendServiceImpl.updateOrDeleteStatusAddFriend(id, status);
    }

    //thong bao trang thai online 
    @MessageMapping("/chat.joinRoom/{roomId}")
    @SendTo("/room/{roomId}")
    public String joinRoom(@Payload String messageOnline, @DestinationVariable String roomId,
            SimpMessageHeaderAccessor headerAccessor) {
        System.out.println(messageOnline);
        return messageOnline;
    }

    //thong bao trang thai online den ban be
    @MessageMapping("/notifyOnline/{userId}")
    @SendTo("/room/user/{userId}")
    public String notifyOnline(@Payload String messageOnline, @DestinationVariable String userId,
            SimpMessageHeaderAccessor headerAccessor) {
        System.out.println(messageOnline);
        return messageOnline;
    }

    //thong bao trang thai online den ban be
    @MessageMapping("/notifyConnect")
    public void notifyConnect(@Payload String messageConnect,
            SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session     
        System.out.println(messageConnect);
        JSONObject jSONObject = new JSONObject(messageConnect);
        int id = jSONObject.getInt("user");
        boolean login = jSONObject.getBoolean("login");
        headerAccessor.getSessionAttributes().put("username", String.valueOf(id));
        userServiceImpl.notifyOnlineToRoomAndFriend(id, login);

        List<Map<String, Object>> messageaUnread = messageServiceImpl.getMessageaUnread(id);
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.UNREAD_MESSAGE.getStatus(),
                        messageaUnread,
                        ResultModelV2.Status.UNREAD_MESSAGE.name(),
                        new Date()));

        // get request_addfriend
        List<Map<String, Object>> requestAddFriend = friendServiceImpl.getRequestAddFriend(id);
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.REQUEST_ADDFRIEND.getStatus(),
                        requestAddFriend,
                        ResultModelV2.Status.REQUEST_ADDFRIEND.name(),
                        new Date()));

        // get response addfriend
        List<Map<String, Object>> responseAddFriend = friendServiceImpl.getResponseAddFriend(id);
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.RESPONSE_ADDFRIEND.getStatus(),
                        responseAddFriend,
                        ResultModelV2.Status.RESPONSE_ADDFRIEND.name(),
                        new Date()));

        // get response disagree addfriend
        List<Map<String, Object>> responseDisAgreeAddFriend = friendServiceImpl.getDisAgreeResponseAddFriend(id);
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.RESPONSE_DISAGREE_ADDFRIEND.getStatus(),
                        responseDisAgreeAddFriend,
                        ResultModelV2.Status.RESPONSE_DISAGREE_ADDFRIEND.name(),
                        new Date()));

        // gui nhung phong da tao trong khi client dang offline
//        List<Map<String, Object>> roomNotReceived = roomServiceImpl.getRoomNotReceived(id);
//        sendingOperations.convertAndSend("/room/user/" + id,
//                new ResultModelV2(ResultModelV2.Status.ROOM_NOT_RECEIVED.getStatus(),
//                        roomNotReceived,
//                        ResultModelV2.Status.ROOM_NOT_RECEIVED.name(),
//                        new Date()));
    }

    //tai tin nhan chua doc (khong con su dung)
    @MessageMapping("/load/messages.unread")
    public void loadMessageUnread(@Payload String json) {
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("id");
        List<Map<String, Object>> messageaUnread = messageServiceImpl.getMessageaUnread(id);
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.UNREAD_MESSAGE.getStatus(),
                        messageaUnread,
                        ResultModelV2.Status.UNREAD_MESSAGE.name(),
                        new Date()));
    }

    //loadmore tin nhan
    @MessageMapping("/loadmore/messages")
    public void loadMessage(@Payload String json) {
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("id");
        int idRoom = jSONObject.getInt("idRoom");
        int start = jSONObject.getInt("indexStart");
        int count = jSONObject.getInt("count");
        List<Map<String, Object>> loadMessages = messageServiceImpl.loadMessages(idRoom, start, count);
        sendingOperations.convertAndSend("/room/user/" + id,
                new ResultModelV2(ResultModelV2.Status.LOADMORE_MESSAGE.getStatus(),
                        loadMessages,
                        ResultModelV2.Status.LOADMORE_MESSAGE.name(),
                        new Date()));
    }

    //kiem tra trang thai tin nhan
    @MessageMapping("/check/messages.status/{roomId}")
    @SendTo("/room/{roomId}")
    public String checkStatusMessage(@Payload String json, @DestinationVariable String roomId) {
        System.out.println(json);
        String checkStatusMessage = messageServiceImpl.checkStatusMessage(json);
        return checkStatusMessage;
    }

    //tim user 
    @MessageMapping("/find.user")
    public void findUser(@Payload String json) {
        // Add username in web socket session     
        System.out.println(json);
        JSONObject jSONObject = new JSONObject(json);
        int id = jSONObject.getInt("id");
        String phone = jSONObject.getString("phone");
        UserModel userByPhone = userServiceImpl.getUserByPhone(phone);
        if (jSONObject.has("caller")) {
            if (userByPhone != null) {
                sendingOperations.convertAndSend("/room/user/" + id,
                        new ResultModelV2(ResultModelV2.Status.FIND_MEMBER_INVITE_ROOM.getStatus(),
                                MappedUserModel.convertToMapBy4Record(userByPhone),
                                ResultModelV2.Status.FIND_MEMBER_INVITE_ROOM.name(),
                                new Date()));
            } else {
                sendingOperations.convertAndSend("/room/user/" + id,
                        new ResultModelV2(ResultModelV2.Status.FIND_MEMBER_INVITE_ROOM.getStatus(),
                                new HashMap(),
                                ResultModelV2.Status.FIND_MEMBER_INVITE_ROOM.name(),
                                new Date()));
            }

        } else {
            sendingOperations.convertAndSend("/room/user/" + id,
                    new ResultModelV2(ResultModelV2.Status.FIND_FRIEND.getStatus(),
                            MappedUserModel.convertToMapBy4Record(userByPhone),
                            ResultModelV2.Status.FIND_FRIEND.name(),
                            new Date()));
        }

    }

    //them thanh vien vao nhom 
    @MessageMapping("/invite/member")
    public void inviteMember(@Payload String json) {
        System.out.println(json);
        roomServiceImpl.inviteMembers(json);
    }

    //roi khoi nhom chat
    @MessageMapping("/leave/room")
    public void leaveRoom(@Payload String json) {
        System.out.println(json);
        roomServiceImpl.leaveRoom(json);
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
