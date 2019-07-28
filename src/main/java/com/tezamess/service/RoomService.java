package com.tezamess.service;

import com.tezamess.model.RoomModel;
import java.util.List;
import java.util.Map;

public interface RoomService {

    RoomModel findRoom(int idRoom);

    void findOrCreateRoom(String json);
    
    void updateRoom(String json);

    void changeStatusReceivedRoom(int id, int idRoom);

    List<Map<String, Object>> getRoomNotReceived(int id);

    void inviteMembers(String json);

    void leaveRoom(String json);
}
