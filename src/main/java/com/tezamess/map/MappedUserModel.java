package com.tezamess.map;

import com.tezamess.model.UserModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappedUserModel {
    
    public static Map<String, Object> convertToMap(UserModel user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("phone", user.getPhone());
        map.put("name", user.getName());
        map.put("password", user.getPassword());
        map.put("birthday", user.getBirthday());
        map.put("gender", user.getGender());
        map.put("urlavatar", user.getUrlavatar());
        map.put("online", user.getOnline());
        map.put("lastactive", user.getLastactive());
        return map;
    }
    
    public static Map<String, Object> convertToMapBy4Record(UserModel user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("phone", user.getPhone());
        map.put("name", user.getName());
        map.put("urlavatar", user.getUrlavatar());
        return map;
    }
    
    public static Map<String, Object> convertToMapWithRooms(UserModel user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("phone", user.getPhone());
        map.put("name", user.getName());
        map.put("password", user.getPassword());
        map.put("birthday", user.getBirthday());
        map.put("gender", user.getGender());
        map.put("urlavatar", user.getUrlavatar());
        map.put("online", user.getOnline());
        map.put("lastactive", user.getLastactive());
        List<Map<String, Object>> rooms = new ArrayList<>();
        user.getRoomModelList().stream().forEach(t -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("creator", t.getCreator().getId());
            m.put("name", t.getName());
            m.put("type", t.getTypeRoomModel().getId());
//            List<Map<String, Object>> members = new ArrayList<>();
//            t.getUserModelList().stream().forEach(s -> {                
//                
//                Map<String, Object> member = convertToMapBy4Record(s);
//                members.add(member);
//            });
            m.put("members", t.getUserModelList().size());
            rooms.add(m);
        });
        map.put("rooms", rooms);
        return map;
    }
}
