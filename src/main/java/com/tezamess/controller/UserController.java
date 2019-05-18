package com.tezamess.controller;

import com.tezamess.model.UserModel;
import com.tezamess.model.WelcomeModel;

import com.tezamess.serviceimpl.UserServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping(value = {"/", "/appchat"})
    public ResponseEntity<Object> welcome() {
        return new ResponseEntity<>(new WelcomeModel("Đỗ Nguyễn Sĩ", "Nguyễn Minh Trí", "Luận văn tốt nghiệp!"), HttpStatus.OK);
    }

    @GetMapping("appchat/api-users")
    public ResponseEntity<List<UserModel>> getAll() {
        List<UserModel> list = userServiceImpl.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("appchat/api-user/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") int id) {
        return userServiceImpl.findUserById(id);
    }

    @PostMapping("appchat/api-login")
    public ResponseEntity<Object> login(@RequestBody(required = false) String json) {
        return userServiceImpl.login(json);

    }

    @PostMapping("appchat/api-register")
    public ResponseEntity<Object> register(@RequestBody(required = false) String json) {
        return userServiceImpl.register(json);
    }

    @PutMapping("appchat/api/update-user")
    public ResponseEntity<Object> updateInfoUser(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {     
        return userServiceImpl.updateUser(token,json);
    }
}
