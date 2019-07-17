package com.tezamess.serviceimpl;

import com.tezamess.map.MappedUserModel;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.ResultModelV2.Status;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.FriendRepositoryImpl;
import com.tezamess.service.FriendService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:message.properties")
public class FriendServiceImpl implements FriendService {

    @Autowired
    private Environment environment;

    @Autowired
    private FriendRepositoryImpl friendRepositoryImpl;

    @Override
    public ResponseEntity<Object> getFriends(String json) {

        JSONObject jSONObject;
        int id;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            jSONObject = new JSONObject(json);
            id = jSONObject.getInt("id");
        } catch (IOException | JSONException ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_JSON.getStatus(), null, environment.getProperty("json.invalid"), new Date()), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResultModelV2(Status.ERROR_SERVER.getStatus(), null, environment.getProperty("error.server"), new Date()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<UserModel> friends = friendRepositoryImpl.getFriends(id);
        List<Map<String, Object>> mappingFriends = new ArrayList<>();
        if (friends.size() > 0) {
            mappingFriends = friends.stream().map(t -> MappedUserModel.convertToMapBy4Record(t)).collect(Collectors.toList());
        }

        return new ResponseEntity<>(new ResultModelV2(ResultModelV2.Status.SUCCESS.getStatus(), mappingFriends, ResultModelV2.Status.SUCCESS.name(), new Date()), HttpStatus.OK);
    }

    @Override
    public void addFriend(int id, int idfriend, int status) {
        if (status == 1) {
            friendRepositoryImpl.addFriend(id, idfriend);
        }
        if (status == -1) {
            friendRepositoryImpl.disAgreeAddFriend(id, idfriend);
        }
    }

    @Override
    public void requestAddFriend(int idUserRequest, int idUserFriend) {
        friendRepositoryImpl.requestAddFriend(idUserRequest, idUserFriend);
    }

    @Override
    public List<Map<String, Object>> getRequestAddFriend(int idUserFriend) {
        List<UserModel> requestAddFriend = friendRepositoryImpl.getRequestAddFriend(idUserFriend);
        return MappedUserModel.convertToListBy5Record(requestAddFriend);
    }

    @Override
    public List<Map<String, Object>> getResponseAddFriend(int idUserRequest) {
        List<UserModel> responseAddFriend = friendRepositoryImpl.getResponseAddFriend(idUserRequest);
        return MappedUserModel.convertToListBy5Record(responseAddFriend);
    }

    @Override
    public List<Map<String, Object>> getDisAgreeResponseAddFriend(int idUserRequest) {
        List<UserModel> responseDisAgreeAddFriend = friendRepositoryImpl.getDisAgreeResponseAddFriend(idUserRequest);
        return MappedUserModel.convertToListBy5Record(responseDisAgreeAddFriend);
    }

    @Override
    public void updateOrDeleteStatusAddFriend(int id, int status) {
        friendRepositoryImpl.updateOrDeleteStatusAddFriend(id, status);
    }

}
