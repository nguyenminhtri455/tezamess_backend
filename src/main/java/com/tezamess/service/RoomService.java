package com.tezamess.service;

import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;

public interface RoomService {
    RoomModel createRoom(String name, UserModel userModel);

    void deleteRoom(int id);

    RoomModel findRoom(int id);
}
