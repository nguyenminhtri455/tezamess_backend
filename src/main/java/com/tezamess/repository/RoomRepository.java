package com.tezamess.repository;

import com.tezamess.model.RoomModel;

public interface RoomRepository {

    RoomModel createRoom(String name, int idCreateUser, int... ids);

    void deleteRoom(int id);

    RoomModel findRoom(int id);
}
