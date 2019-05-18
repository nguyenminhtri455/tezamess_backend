package com.tezamess.service;

import com.tezamess.model.UserModel;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface UserService {

    List<UserModel> findAll();

    ResponseEntity<Object> login(String json);

    ResponseEntity<Object> register(String json);

    ResponseEntity<Object> findUserById(int id);

    UserModel getUserByPhone(String phone);

    ResponseEntity<Object> updateUser(String token, String json);

    ResponseEntity<Object> findUserByPhone(String token, String json);
}
