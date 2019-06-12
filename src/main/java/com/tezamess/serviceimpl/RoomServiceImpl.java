package com.tezamess.serviceimpl;

import com.tezamess.model.RoomModel;
import com.tezamess.repositoryimpl.RoomRepositoryImpl;
import com.tezamess.service.RoomService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepositoryImpl roomRepositoryImpl;

    @Override
    public RoomModel createRoom(String json) {
        JSONObject jSONObject = new JSONObject(json);
        JSONArray jsonArray = jSONObject.getJSONArray("ids");
        RoomModel room = roomRepositoryImpl.
                createRoom(jSONObject.getString("name"),
                        jSONObject.getInt("idCreateUser"),
                        new int[]{jsonArray.getInt(0)});
        return room;
    }

    @Override
    public void deleteRoom(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
