package com.tezamess.map;

import com.tezamess.model.RoomModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappedRoomModel {

    public static Map<String, Object> convertToMap(RoomModel room) {
//        List<Map<String, Object>> userInRoom = new ArrayList<>();
//        room.getUserModelList().stream().forEach(t -> {
//            userInRoom.add(MappedUserModel.convertToMapBy4Record(t));
//        });
//        Map<String, Object> map = new HashMap<>();
//        map.put("id", room.getId());
//        map.put("name", room.getName());
//        map.put("creator", room.getCreator().getId());
//        map.put("users", userInRoom);
//        map.put("type", room.getTypeRoomModel().getId());
//        return map;

//--------------------
        List<Map<String, Object>> userInRoom = new ArrayList<>();
        room.getParticipationModels().stream().forEach(t -> {
            userInRoom.add(MappedUserModel.convertToMapBy4Record(t.getUser()));
        });
        Map<String, Object> map = new HashMap<>();
        map.put("id", room.getId());
        map.put("name", room.getName());
        map.put("creator", room.getCreator().getId());
        map.put("users", userInRoom);
        map.put("type", room.getTypeRoomModel().getId());     
        return map;
    }

    public static List<Map<String, Object>> convertToMap(List<RoomModel> rooms) {
        List<Map<String, Object>> mapRooms = new ArrayList<>();
        rooms.stream().forEach(room -> {        
                Map<String, Object> mapRoom = convertToMap(room);
                mapRooms.add(mapRoom);      
        });
        return mapRooms;
    }
}
