package com.tezamess.serviceimpl;

import com.tezamess.map.MappedRoomModel;
import com.tezamess.map.MappedUserModel;
import com.tezamess.model.ParticipationModel;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.RoomModel;
import com.tezamess.repositoryimpl.RoomRepositoryImpl;
import com.tezamess.service.RoomService;
import com.tezamess.utils.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private FileUtils fileUtils;

    @Override
    public void findOrCreateRoom(String json) {
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
                    if (jSONObject.has("avatar")) {
                        String avatar = jSONObject.getString("avatar");
                        JSONObject image = new JSONObject(avatar);
                        String urlAvatar = fileUtils.saveImage(image.getString("valueBase64"),
                                image.getString("name"));
                        room = roomRepositoryImpl.
                                createRoom(nameRoom,
                                        urlAvatar,
                                        creatorId,
                                        listId);
                    } else {
                        room = roomRepositoryImpl.
                                createRoom(nameRoom,
                                        creatorId,
                                        listId);
                    }

                } else {
//                    room = roomRepositoryImpl.findRoom(listId);
                    if (room == null) {
                        room = roomRepositoryImpl.
                                createRoom(nameRoom,
                                        creatorId,
                                        listId);
                    }
                }
            }
            if (room != null) {
                Set<ParticipationModel> listUser = room.getParticipationModels();
                for (ParticipationModel t : listUser) {
                    System.out.println(t.getUser().getId() + "----id nhan duoc thong bao tao room");
                    messagingTemplate.convertAndSend("/room/user/" + t.getUser().getId(),
                            new ResultModelV2(ResultModelV2.Status.CREATE_ROOM.getStatus(),
                                    MappedRoomModel.convertToMap(room),
                                    ResultModelV2.Status.CREATE_ROOM.name(),
                                    new Date()));
                }
            } else {
                System.out.println("loi !!!!!");
            }
        } catch (IOException | JSONException ex) {
            System.out.println(ex.getMessage() + " catch 1");

        } catch (Exception ex) {
            System.out.println(ex.getMessage() + " catch 2");

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

    @Override
    public void updateRoom(String json) {
        JSONObject jSONObject = new JSONObject(json);
        int idRoom = jSONObject.getInt("idRoom");
        int idUser = jSONObject.getInt("sender");
        RoomModel roomModel = new RoomModel(idRoom);
        String name, avatar;
        if (jSONObject.has("name")) {
            name = jSONObject.getString("name");
            roomModel.setName(name);
        }

        if (jSONObject.has("avatar")) {
            avatar = jSONObject.getString("avatar");
            JSONObject image = new JSONObject(avatar);
            String urlAvatar = fileUtils.saveImage(image.getString("valueBase64"),
                    image.getString("name"));
            roomModel.setAvatar(urlAvatar);
        }

        RoomModel room = roomRepositoryImpl.updateRoom(roomModel);
        if (room != null) {
            Set<ParticipationModel> listUser = room.getParticipationModels();
            listUser.stream().map((t) -> {
                System.out.println(t.getUser().getId() + "----id nhan duoc thong bao tao room");
                return t;
            }).forEachOrdered((t) -> {
                Map<String, Object> convertToMapOnlyRoom = MappedRoomModel.convertToMapOnlyRoom(room);
                convertToMapOnlyRoom.put("sender", idUser);
                messagingTemplate.convertAndSend("/room/user/" + t.getUser().getId(),
                        new ResultModelV2(ResultModelV2.Status.UPDATE_ROOM.getStatus(),
                                convertToMapOnlyRoom,
                                ResultModelV2.Status.UPDATE_ROOM.name(),
                                new Date()));
            });
        } else {
            System.out.println("loi !!!!!");
        }
    }
}
