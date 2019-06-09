/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tezamess.map;

import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author user
 */
public class MappedRoomModel {

    public static Map<String, Object> convertToMap(RoomModel room) {
        Map<String, Object> createUser = MappedUserModel.convertToMapBy4Record(room.getCreator());
        List<Map<String, Object>> userInRoom = new ArrayList<>();
        room.getUserModelList().stream().forEach(t -> {
            userInRoom.add(MappedUserModel.convertToMapBy4Record(t));
        });

        Map<String, Object> map = new HashMap<>();
        map.put("id", room.getId());
        map.put("name", room.getName());
        map.put("creator", createUser);
        map.put("users", userInRoom);
        return map;
    }
}
