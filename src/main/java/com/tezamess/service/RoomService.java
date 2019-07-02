package com.tezamess.service;

import com.tezamess.model.RoomModel;

public interface RoomService {
    RoomModel findRoom(int idRoom);
    RoomModel findOrCreateRoom(String json);
}
