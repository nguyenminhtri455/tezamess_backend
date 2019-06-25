package com.tezamess.map;

import com.tezamess.model.MessageModel;
import com.tezamess.model.UserModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        map.put("lastactive", user.getLastactive().getTime());
        return map;
    }

    public static List<Map<String, Object>> convertToListBy5Record(UserModel user1, UserModel user2) {
        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> map = new HashMap<>();
        map.put("idRequest", user1.getId());
        map.put("phone", user1.getPhone());
        map.put("name", user1.getName());
        map.put("urlavatar", user1.getUrlavatar());
        map.put("lastactive", user1.getLastactive().getTime());
        list.add(map);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("idFriend", user2.getId());
        map2.put("phone", user2.getPhone());
        map2.put("name", user2.getName());
        map2.put("urlavatar", user2.getUrlavatar());
        map2.put("lastactive", user2.getLastactive().getTime());
        list.add(map2);
        return list;
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
        user.getRoomModelList().stream()
                .forEach(t -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", t.getId());
                    m.put("creator", t.getCreator().getId());
                    m.put("name", t.getName());
                    m.put("type", t.getTypeRoomModel().getId());
                    m.put("size", t.getUserModelList().size());
//                    if (t.getTypeRoomModel().getId().equals("D")) {
                    //Danh sach thanh vien trong phong
                    List<Map<String, Object>> members = new ArrayList<>();
                    t.getUserModelList().stream().forEach(s -> {
                        Map<String, Object> member = convertToMapBy4Record(s);
                        members.add(member);
                    });
                    m.put("members", members);
//                    }
                    rooms.add(m);

                    //Danh sach tin nhan trong phong
                    List<Map<String, Object>> messages = new ArrayList<>();
                    t.getMessageList().stream()
                            .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()))
                            //                            .sorted(Comparator.comparingInt(MessageModel::getId).reversed())
                            .forEach(a -> {
                                Map<String, Object> message = MappedMessageModel.convertMessageChat(a);
                                messages.add(message);
                            });
                    m.put("messages", messages);
                });
        map.put("rooms", rooms);
        return map;
    }
}
