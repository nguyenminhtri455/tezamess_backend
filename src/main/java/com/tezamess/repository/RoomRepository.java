package com.tezamess.repository;

import com.tezamess.model.RoomModel;
import java.util.List;

public interface RoomRepository {

    RoomModel createRoom(String name, int idCreateUser, List<Integer> listId);

    void deleteRoom(int id);

    RoomModel findRoom(int id);

    RoomModel findRoom(List<Integer> listId);
}
