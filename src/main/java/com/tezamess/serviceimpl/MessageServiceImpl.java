package com.tezamess.serviceimpl;

import com.tezamess.map.MappedMessageModel;
import com.tezamess.model.MessageModel;
import com.tezamess.model.RoomModel;
import com.tezamess.model.TempMessageModel;
import com.tezamess.model.TypeMessageModel;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.MessageRepositoryImpl;
import com.tezamess.service.MessageService;
import com.tezamess.utils.FileUtils;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepositoryImpl messageRepositoryImpl;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void saveMessage(String json) {
        JSONObject jSONObject;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            jSONObject = new JSONObject(json);

            MessageModel messageModel = new MessageModel();
            long time = jSONObject.getLong("createdate");
            String body = jSONObject.getString("body");
            int roomId = jSONObject.getInt("room");
            int senderId = jSONObject.getInt("user");
            String type = jSONObject.getString("type");

            UserModel userModel = new UserModel(senderId);
            messageModel.setCreatedate(new Date(time));

            messageModel.setRoomid(new RoomModel(roomId));
            messageModel.setUserid(userModel);
            switch (type) {
                case "Chat":
                    messageModel.setBody(body);
                    messageModel.setTypeMessageModel(new TypeMessageModel("C"));
                    break;
                case "Image":
                    JSONObject image = new JSONObject(body);
                    String urlAvatar = fileUtils.saveImage(image.getString("valueBase64"),
                            image.getString("name"));
                    messageModel.setBody(urlAvatar);
                    messageModel.setTypeMessageModel(new TypeMessageModel("I"));
                    break;
                case "File":
                    messageModel.setTypeMessageModel(new TypeMessageModel("F"));
                    break;
            }

            MessageModel message = messageRepositoryImpl.saveMessage(messageModel);
            
            messagingTemplate.convertAndSend("/room/" + roomId,
                    MappedMessageModel.convertToJsonMessageChat(messageModel));

        } catch (IOException | JSONException ex) {
            System.out.println(ex.getMessage() + " catch saveMessage 1");
        } catch (Exception ex) {
            System.out.println(ex.getMessage() + " catch saveMessage 2");
        }
    }

    @Override
    public List<Map<String, Object>> getMessageaUnread(int id) {
        List<MessageModel> messagesUnread = messageRepositoryImpl.getMessagesUnread(id);
        List<Map<String, Object>> convertListMessageChat = MappedMessageModel.convertListMessageChat(messagesUnread);
        return convertListMessageChat;
    }

    @Override
    public String updateStatusMessage(String json) {
        JSONObject jSONObject;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            jSONObject = new JSONObject(json);

            TempMessageModel tempMessageModel = new TempMessageModel();
            int messageId = jSONObject.getInt("id");
            int roomId = jSONObject.getInt("room");
            int receiver = jSONObject.getInt("receiver");
            String status = jSONObject.getString("status");
            switch (status) {
                case "Received":
                    tempMessageModel.setStatusMessage(1);
                    break;
                case "Seen":
                    tempMessageModel.setStatusMessage(2);
                    break;
            }

            tempMessageModel.setIdmessage(new MessageModel(messageId));
            tempMessageModel.setIdRoom(new RoomModel(roomId));
            tempMessageModel.setIdmember(new UserModel(receiver));

            int updateStatusMessage = messageRepositoryImpl.updateStatusMessage(tempMessageModel);
            switch (updateStatusMessage) {
                case 0:
                    jSONObject.put("status", "Sent");
                    break;
                case 1:
                    jSONObject.put("status", "Received");
                    break;
                case 2:
                    jSONObject.put("status", "Seen");
                    break;
            }
            return jSONObject.toString();
        } catch (IOException | JSONException ex) {
            System.out.println(ex.getMessage());
            return json;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return json;
        }
    }

    @Override
    public String checkStatusMessage(String json) {
        JSONObject jSONObject;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            jSONObject = new JSONObject(json);
            MessageModel messageModel = new MessageModel();
            int id = jSONObject.getInt("id");
            int roomId = jSONObject.getInt("room");
            int senderId = jSONObject.getInt("user");

            messageModel.setId(id);
            messageModel.setRoomid(new RoomModel(roomId));
            messageModel.setUserid(new UserModel(senderId));
            TempMessageModel checkStatusMessage = messageRepositoryImpl.checkStatusMessage(messageModel);
            if (checkStatusMessage != null) {
                return MappedMessageModel.convertToJsonMessageChat(checkStatusMessage);
            }
        } catch (IOException | JSONException ex) {
            System.out.println(ex.getMessage());
            return json;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return json;
        }
        return json;
    }

    @Override
    public List<Map<String, Object>> loadMessages(int idRoom, int start, int count) {
        List<MessageModel> loadMessages = messageRepositoryImpl.loadMessages(idRoom, start, count);
        return MappedMessageModel.convertListMessageChatNoStatus(loadMessages);
    }

}
