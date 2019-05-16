package com.appchat.controller;

import com.appchat.model.UserModel;

import com.appchat.serviceimpl.UserServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class MainController {

    @Autowired
    private UserServiceImpl userServiceImpl;


    @GetMapping("appchat/api-welcome")
    public ResponseEntity<List<UserModel>> getAll() {
        List<UserModel> list = userServiceImpl.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("appchat/api-login")
    public ResponseEntity<Object> login(@RequestBody(required = false) String json) {
        return userServiceImpl.login(json);

    }

    @PostMapping("appchat/api-register")
    public ResponseEntity<Object> register(@RequestBody(required = false) String json) { 
        return userServiceImpl.register(json);
    }

    @PutMapping("appchat/api-update")
    public ResponseEntity<Object> put(@RequestBody String jsonUser, UriComponentsBuilder builder) {
       
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
