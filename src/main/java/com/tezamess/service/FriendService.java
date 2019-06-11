package com.tezamess.service;

import org.springframework.http.ResponseEntity;

public interface FriendService {
    ResponseEntity<Object> getFriends(String json);
}
