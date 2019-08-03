package com.tezamess.repository;

import com.tezamess.model.FriendModel;
import com.tezamess.model.UserModel;
import java.util.List;

public interface FriendRepository {

    List<UserModel> getFriends(int idUser);

    void addFriendAdmin(int id, int idfriend);

    void addFriend(int id, int idfriend);

    void unFriend(int id, int idfriend);

    int cancelRequestAddFriend(int id, int idfriend);

    void disAgreeAddFriend(int id, int idfriend);

    void requestAddFriend(int idUserRequest, int idUserFriend);

    List<UserModel> getRequestAddFriend(int idUserFriend);

    List<UserModel> getResponseAddFriend(int idUserRequest);

    void updateOrDeleteStatusAddFriend(int id, int status);

    List<UserModel> getUserSentRequestAddFriend(Integer id);

    List<UserModel> getDisAgreeResponseAddFriend(int idUserRequest);
}
