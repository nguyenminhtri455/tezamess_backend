package com.tezamess.serviceimpl;

import com.tezamess.map.MappedMessageModel;
import com.tezamess.model.MessageModel;
import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.MessageRepositoryImpl;
import com.tezamess.service.MessageService;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepositoryImpl messageRepositoryImpl;

    @Override
    public String saveMessage(String json) {
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
            
            UserModel userModel = new UserModel(senderId);
            messageModel.setCreatedate(new Date(time));
            messageModel.setBody(body);
            messageModel.setRoomid(new RoomModel(roomId));
            messageModel.setUserid(userModel);
                       
            MessageModel message = messageRepositoryImpl.saveMessage(messageModel);
            
            
            
            return MappedMessageModel.convertToJsonMessageChat(messageModel);
        } catch (IOException | JSONException ex) {
            System.out.println(ex.getMessage());
            return null;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
             return null;
        }
    }

    @Override
    public List<Map<String, Object>> getMessageaUnread(int id) {
        List<Object[]> messagesUnread = messageRepositoryImpl.getMessagesUnread(id);
        List<Map<String, Object>> convertListMessageChat = MappedMessageModel.convertListMessageChat(messagesUnread);
        return null;
    }

}
