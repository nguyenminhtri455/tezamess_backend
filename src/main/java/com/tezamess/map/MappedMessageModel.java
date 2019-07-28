package com.tezamess.map;

import com.tezamess.model.MessageModel;
import com.tezamess.model.TempMessageModel;
import com.tezamess.model.TypeMessageModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class MappedMessageModel {

    public static String convertToJsonMessageChat(MessageModel messageModel) {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("id", messageModel.getId());
        jSONObject.put("createdate", messageModel.getCreatedate().getTime());
        jSONObject.put("body", messageModel.getBody());
        jSONObject.put("user", messageModel.getUserid().getId());
        jSONObject.put("room", messageModel.getRoomid().getId());
        jSONObject.put("status", "Sent");
        switch (messageModel.getTypeMessageModel().getId()) {
            case "C":
                jSONObject.put("type", "Chat");
                break;
            case "I":
                jSONObject.put("type", "Image");
                break;
            case "F":
                jSONObject.put("type", "File");
                break;
        }

        return jSONObject.toString();
    }

    public static String convertToJsonMessageChat(TempMessageModel tempMessageModel) {
        JSONObject jSONObject = new JSONObject();
        MessageModel messageModel = tempMessageModel.getIdmessage();
        jSONObject.put("id", messageModel.getId());
        jSONObject.put("createdate", messageModel.getCreatedate().getTime());
        jSONObject.put("body", messageModel.getBody());
        jSONObject.put("user", messageModel.getUserid().getId());
        jSONObject.put("room", messageModel.getRoomid().getId());

        switch (messageModel.getTypeMessageModel().getId()) {
            case "C":
                jSONObject.put("type", "Chat");
                break;
            case "I":
                jSONObject.put("type", "Image");
                break;
            case "F":
                jSONObject.put("type", "File");
                break;
        }

        switch (tempMessageModel.getStatusMessage()) {
            case -1:
                jSONObject.put("status", "Unread");
                break;
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
    }

    public static Map<String, Object> convertMessageChat(MessageModel messageModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", messageModel.getId());
        map.put("createdate", messageModel.getCreatedate().getTime());
        map.put("body", messageModel.getBody());
        map.put("user", messageModel.getUserid().getId());
        map.put("room", messageModel.getRoomid().getId());
        map.put("status", "Sent");

        switch (messageModel.getTypeMessageModel().getId()) {
            case "C":
                map.put("type", "Chat");
                break;
            case "I":
                map.put("type", "Image");
                break;
            case "F":
                map.put("type", "File");
                break;
        }
        return map;
    }

    public static List<Map<String, Object>> convertListMessageChat(List<MessageModel> messageModels) {
        List<Map<String, Object>> list = new ArrayList<>();
        messageModels.stream().forEach(t -> {
            Map<String, Object> m1 = new HashMap<>();
            m1.put("id", t.getId());
            m1.put("body", t.getBody());
            m1.put("createdate", t.getCreatedate().getTime());
            m1.put("room", t.getRoomid().getId());
            m1.put("user", t.getUserid().getId());
            m1.put("status", "Received");
            switch (t.getTypeMessageModel().getId()) {
                case "C":
                    m1.put("type", "Chat");
                    break;
                case "I":
                    m1.put("type", "Image");
                    break;
                case "F":
                    m1.put("type", "File");
                    break;
            }
            list.add(m1);
        });
        return list;
    }

    public static List<Map<String, Object>> convertListMessageChatNoStatus(List<MessageModel> messageModels) {
        List<Map<String, Object>> list = new ArrayList<>();

        messageModels.stream()
                .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()))
                .forEach(t -> {
                    Map<String, Object> m1 = new HashMap<>();
                    m1.put("id", t.getId());
                    m1.put("body", t.getBody());
                    m1.put("createdate", t.getCreatedate().getTime());
                    m1.put("room", t.getRoomid().getId());
                    m1.put("user", t.getUserid().getId());
                    list.add(m1);
                });
        return list;
    }

}
