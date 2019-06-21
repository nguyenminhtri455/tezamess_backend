package com.tezamess.map;

import com.tezamess.model.MessageModel;
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
        jSONObject.put("type", "Chat");
        return jSONObject.toString();
    }

    public static Map<String, Object> convertMessageChat(MessageModel messageModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", messageModel.getId());
        map.put("createdate", messageModel.getCreatedate().getTime());
        map.put("body", messageModel.getBody());
        map.put("user", messageModel.getUserid().getId());
        map.put("room", messageModel.getRoomid().getId());
        map.put("status", "Seen");
        map.put("type", "Chat");
        return map;
    }

    public static List<Map<String, Object>> convertListMessageChat(List<Object[]> messageModel) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        list.add(map);
        return list;
    }

}
