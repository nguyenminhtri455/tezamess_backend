package com.tezamess.service;

import com.tezamess.model.RoomModel;

public interface RoomService {
    RoomModel createRoom(String json);
    RoomModel findOrCreateRoom(String json);
    void deleteRoom(int id);

}
