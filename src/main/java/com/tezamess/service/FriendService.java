package com.tezamess.service;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface FriendService {

    ResponseEntity<Object> getFriends(String json);

    void addFriend(int id, int idfriend, int status);

    void unFriend(int id, int idfriend);

    void requestAddFriend(int idUserRequest, int idUserFriend);

    List<Map<String, Object>> getRequestAddFriend(int idUserFriend);

    List<Map<String, Object>> getResponseAddFriend(int idUserRequest);

    List<Map<String, Object>> getDisAgreeResponseAddFriend(int idUserRequest);

    void updateOrDeleteStatusAddFriend(int id, int status);
}
