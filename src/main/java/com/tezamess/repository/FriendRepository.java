package com.tezamess.repository;

import com.tezamess.model.UserModel;
import java.util.List;

public interface FriendRepository {
    List<UserModel> getFriends(int idUser);
}
