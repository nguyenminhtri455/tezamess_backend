package com.tezamess.controller;

import com.tezamess.map.MappedRoomModel;
import com.tezamess.map.MappedUserModel;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.ResultModelV2.Status;
import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.model.WelcomeModel;
import com.tezamess.repositoryimpl.FriendRepositoryImpl;
import com.tezamess.serviceimpl.FriendServiceImpl;
import com.tezamess.serviceimpl.RoomServiceImpl;

import com.tezamess.serviceimpl.UserServiceImpl;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController implements ErrorController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private RoomServiceImpl roomServiceImpl;

    @Autowired
    private FriendServiceImpl friendServiceImpl;

    @GetMapping(value = {"/", "/tezamess"})
    public ResponseEntity<Object> welcome() {
        return new ResponseEntity<>(new WelcomeModel("Đỗ Nguyễn Sĩ", "Nguyễn Minh Trí", "Luận văn tốt nghiệp!"), HttpStatus.OK);
    }

    @GetMapping("tezamess/api-users")
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> list = userServiceImpl.findAll();
        return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), list, Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }

    @GetMapping("tezamess/api-user/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") int id) {
        return userServiceImpl.findUserById(id);
    }

    @PostMapping("tezamess/api-login")
    public ResponseEntity<Object> login(@RequestBody(required = false) String json) {
        System.out.println(json);
        return userServiceImpl.login(json);
    }

    @PostMapping(value = "tezamess/api-register", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> register(@RequestBody(required = false) String json, HttpServletRequest request) {
        System.out.println(json);
        return userServiceImpl.register(json);
    }

    @PostMapping("tezamess/api/search-user")
    public ResponseEntity<Object> findUserByPhone(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {
        return userServiceImpl.findUserByPhone(token, json);
    }

    @PostMapping("tezamess/api/user-using-app")
    public ResponseEntity<Object> usersUsingApp(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {
        return userServiceImpl.userUsingApp(token, json);
    }

    @PutMapping("tezamess/api/update-user")
    public ResponseEntity<Object> updateInfoUser(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {
        return userServiceImpl.updateUser(token, json);
    }

    @PutMapping("tezamess/api/change-password")
    public ResponseEntity<Object> changePassword(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {
        return userServiceImpl.changePassword(token, json);
    }
    
    @PutMapping("tezamess/api/update-email")
    public ResponseEntity<Object> updateEmail(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {
        return userServiceImpl.updateEmail(token, json);
    }

    @PostMapping("tezamess/api/get-friends")
    public ResponseEntity<Object> getFriends(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {
        return friendServiceImpl.getFriends(json);
    }

    //test
    @Autowired
    FriendRepositoryImpl friendRepositoryImpl;

    @GetMapping("/tezamess/api-test")
    public ResponseEntity<Object> testAPI(@RequestBody(required = false) String json, @RequestHeader(value = "authorization") String token) {
        return userServiceImpl.changePassword(token, json);

    }

    @GetMapping("/error")
    public ResponseEntity<Object> error() {
        return new ResponseEntity<>(new ResultModelV2(Status.ERROR_NOT_FOUND.getStatus(), null, Status.ERROR_NOT_FOUND.name(), new Date()), HttpStatus.NOT_FOUND);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
