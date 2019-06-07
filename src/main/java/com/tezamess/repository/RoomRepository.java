package com.tezamess.repository;

import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;

public interface RoomRepository {

    RoomModel createRoom(String name, UserModel userModel);

    void deleteRoom(int id);

    RoomModel findRoom(int id);
}
