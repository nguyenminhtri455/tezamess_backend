package com.tezamess.service;

import com.tezamess.model.UserModel;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface UserService {

    List<Map<String, Object>> findAll();

    ResponseEntity<Object> login(String json);

    ResponseEntity<Object> register(String json);

    ResponseEntity<Object> findUserById(int id);

    UserModel getUserByPhone(String phone);

    UserModel getUserById(int id);

    ResponseEntity<Object> updateUser(String token, String json);

    ResponseEntity<Object> changePassword(String token, String json);

    ResponseEntity<Object> updateEmail(String token, String json);

    ResponseEntity<Object> findUserByPhone(String token, String json);

    ResponseEntity<Object> userUsingApp(String token, String json);

    void notifyOnlineToRoomAndFriend(int userId, boolean login);

    ResponseEntity<Object> getResetCode(String json);

    ResponseEntity<Object> recoverPassword(String json);

    void sendEmail(String to, String subject, String msg);
}
