package com.tezamess.repository;

import com.tezamess.model.UserModel;
import java.util.List;

public interface FriendRepository {

    List<UserModel> getFriends(int idUser);

    void addFriend(int id, int idfriend);
    
    void disAgreeAddFriend(int id, int idfriend);

    void requestAddFriend(int idUserRequest, int idUserFriend);
    
    List<UserModel> getRequestAddFriend(int idUserFriend);
    
    List<UserModel> getResponseAddFriend(int idUserRequest);
    
    void removeTempFriend(int id, int status);

    List<UserModel> getUserSentRequestAddFriend(Integer id);
    
    List<UserModel> getDisAgreeResponseAddFriend(int idUserRequest);
}
