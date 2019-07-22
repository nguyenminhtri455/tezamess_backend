package com.tezamess.serviceimpl;

import com.tezamess.map.MappedRoomModel;
import com.tezamess.map.MappedUserModel;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.RoomModel;
import com.tezamess.repositoryimpl.RoomRepositoryImpl;
import com.tezamess.service.RoomService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepositoryImpl roomRepositoryImpl;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
                String nameRoom = jSONObject.getString("name").trim();
                int creatorId = jSONObject.getInt("creator");
                listId.add(creatorId);
                for (int i = 0; i < jSONArray.length(); i++) {
                    listId.add(jSONArray.getInt(i));
                }
                if (!nameRoom.isEmpty()) {
                    room = roomRepositoryImpl.
                            createRoom(nameRoom,
                                    creatorId,
                                    listId);
                } else {
                    room = roomRepositoryImpl.findRoom(listId);
                    if (room != null) {
                        return room;
                    }

                    room = roomRepositoryImpl.
                            createRoom(nameRoom,
                                    creatorId,
                                    listId);
                }

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
    public RoomModel findRoom(int idRoom) {
        RoomModel room = roomRepositoryImpl.findRoom(idRoom);
        return room;
    }

    @Override
    public void changeStatusReceivedRoom(int id, int idRoom) {
        roomRepositoryImpl.changeStatusReceivedRoom(id, idRoom);
    }

    @Override
    public List<Map<String, Object>> getRoomNotReceived(int id) {
        List<Map<String, Object>> convertToMap = MappedRoomModel.convertToMap(roomRepositoryImpl.getRoomNotReceived(id));
        return convertToMap;
    }

    @Override
    public void inviteMembers(String json) {
        JSONObject jSONObject = new JSONObject(json);
        int idRoom = jSONObject.getInt("idRoom");
        List<Integer> ids = new ArrayList();
        JSONArray jsonArray = jSONObject.getJSONArray("ids");
        for (int i = 0; i < jsonArray.length(); i++) {
            ids.add(jsonArray.getInt(i));
        }
        RoomModel inviteMember = roomRepositoryImpl.inviteMember(idRoom, ids);
        Map<String, Object> convertToMap = MappedRoomModel.convertToMap(inviteMember);

        inviteMember.getParticipationModels().stream().forEach(t -> {
            messagingTemplate.convertAndSend("/room/user/" + t.getUser().getId(),
                    new ResultModelV2(ResultModelV2.Status.INVITE_MEMBER.getStatus(),
                            convertToMap, ResultModelV2.Status.INVITE_MEMBER.name(),
                            new Date()));
        });
    }

    @Override
    public void leaveRoom(String json) {
        JSONObject jSONObject = new JSONObject(json);
        int idRoom = jSONObject.getInt("idRoom");
        int idUser = jSONObject.getInt("idUser");

        RoomModel leaveRoom = roomRepositoryImpl.leaveRoom(idRoom, idUser);
        Map<String, Object> convertToMap = MappedRoomModel.convertToMap(leaveRoom);

        leaveRoom.getParticipationModels().stream().forEach(t -> {
            messagingTemplate.convertAndSend("/room/user/" + t.getUser().getId(),
                    new ResultModelV2(ResultModelV2.Status.LEAVE_ROOM.getStatus(),
                            convertToMap, ResultModelV2.Status.LEAVE_ROOM.name(),
                            new Date()));
        });
        messagingTemplate.convertAndSend("/room/user/" + idUser,
                new ResultModelV2(ResultModelV2.Status.LEAVE_ROOM.getStatus(),
                        convertToMap, ResultModelV2.Status.LEAVE_ROOM.name(),
                        new Date()));
    }
}
