package com.appchat.service;

import com.appchat.model.UserModel;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface UserService {

    List<UserModel> findAll();

    ResponseEntity<Object> login(String json);

    ResponseEntity<Object> register(String json);

    UserModel updateUser(UserModel user);

    UserModel findUserByPhone(String phone);
}
