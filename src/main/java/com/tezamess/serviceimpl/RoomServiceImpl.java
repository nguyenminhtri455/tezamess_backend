package com.tezamess.serviceimpl;

import com.tezamess.model.RoomModel;
import com.tezamess.repositoryimpl.RoomRepositoryImpl;
import com.tezamess.service.RoomService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepositoryImpl roomRepositoryImpl;

    @Override
    public RoomModel createRoom(String json) {

        return null;
    }

    @Override
    public RoomModel findOrCreateRoom(String json) {
        JSONObject jSONObject;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            RoomModel room = null;
            jSONObject = new JSONObject(json);
            JSONArray jSONArray = jSONObject.getJSONArray("ids");
            List<Integer> listId;
            if (jSONArray.length() > 0) {
                listId = new ArrayList<>();
                 String nameRoom = jSONObject.getString("name");
                int creatorId = jSONObject.getInt("creator");
                listId.add(creatorId);
                for (int i = 0; i < jSONArray.length(); i++) {
                    listId.add(jSONArray.getInt(i));
                }
                room = roomRepositoryImpl.findRoom(listId);
                if (room != null) {
                    return room;
                }            
                room = roomRepositoryImpl.
                        createRoom(nameRoom,
                                creatorId,
                                listId);
            }
            return room;
        } catch (IOException | JSONException ex) {
            System.out.println(ex.getMessage());
            return null;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    @Override
    public void deleteRoom(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
