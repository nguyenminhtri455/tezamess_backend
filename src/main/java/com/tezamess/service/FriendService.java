package com.tezamess.service;

import org.springframework.http.ResponseEntity;

public interface FriendService {
    ResponseEntity<Object> getFriends(String json);
    void addFriend(int id, int idfriend,int status);
}
