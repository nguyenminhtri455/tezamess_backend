package com.tezamess.controller;

import com.tezamess.model.ResultModelV2;
import com.tezamess.model.ResultModelV2.Status;
import com.tezamess.model.UserModel;
import com.tezamess.model.WelcomeModel;

import com.tezamess.serviceimpl.UserServiceImpl;
import java.util.Date;
import java.util.List;
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
public class UserController implements ErrorController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @GetMapping(value = {"/", "/tezamess"})
    public ResponseEntity<Object> welcome() {
        return new ResponseEntity<>(new WelcomeModel("Đỗ Nguyễn Sĩ", "Nguyễn Minh Trí", "Luận văn tốt nghiệp!"), HttpStatus.OK);
    }

    @GetMapping("tezamess/api-users")
    public ResponseEntity<Object> getAll() {
        List<UserModel> list = userServiceImpl.findAll();
        return new ResponseEntity<>(new ResultModelV2(Status.SUCCESS.getStatus(), list, Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }

    @GetMapping("tezamess/api-user/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") int id) {
        return userServiceImpl.findUserById(id);
    }

    @PostMapping("tezamess/api-login")
    public ResponseEntity<Object> login(@RequestBody(required = false) String json) {
        return userServiceImpl.login(json);
    }

    @PostMapping("tezamess/api-register")
    public ResponseEntity<Object> register(@RequestBody(required = false) String json) {
        System.out.println(json);
        return userServiceImpl.register(json);
    }

    @PostMapping("tezamess/api/search-user")
    public ResponseEntity<Object> findUserByPhone(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {
        return userServiceImpl.findUserByPhone(token, json);
    }

    @PutMapping("tezamess/api/update-user")
    public ResponseEntity<Object> updateInfoUser(@RequestHeader(value = "authorization") String token, @RequestBody(required = false) String json) {
        return userServiceImpl.updateUser(token, json);
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
