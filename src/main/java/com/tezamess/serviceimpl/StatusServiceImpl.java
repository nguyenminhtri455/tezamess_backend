package com.tezamess.serviceimpl;

import com.tezamess.map.MappedStatusModel;
import com.tezamess.model.ResultModelV2;
import com.tezamess.model.StatusModel;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.FriendRepositoryImpl;
import com.tezamess.repositoryimpl.StatusRepositoryImpl;
import com.tezamess.service.StatusService;
import com.tezamess.utils.FileUtils;
import java.io.IOException;
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
public class StatusServiceImpl implements StatusService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StatusRepositoryImpl statusRepositoryImpl;

    @Autowired
    private FriendRepositoryImpl friendRepositoryImpl;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public void postStatus(String json) {
        JSONObject jSONObject;
        try {
            //kiem tra input data khac null
            if (json == null) {
                throw new IOException();
            }
            jSONObject = new JSONObject(json);
            StatusModel statusModel = new StatusModel();
            int userId = jSONObject.getInt("userid");
            String body = jSONObject.getString("body");
            long datePost = jSONObject.getLong("createdate");
            if (jSONObject.has("images")) {
                JSONArray images = jSONObject.getJSONArray("images");
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.getJSONObject(i);
                    String urlImage = fileUtils.saveImage(image.getString("valueBase64"),
                            image.getString("name"));
                    statusModel.getListMediaModel().add(urlImage);
                }
            }
            statusModel.setUserPostStatus(new UserModel(userId));
            statusModel.setBody(body);
            statusModel.setCreatedate(new Date(datePost));

            StatusModel postStatus = statusRepositoryImpl.postStatus(statusModel);

            messagingTemplate.convertAndSend("/room/user/" + userId,
                    new ResultModelV2(ResultModelV2.Status.POST_STATUS.getStatus(),
                            MappedStatusModel.convertStatusModelToJson(postStatus),
                            ResultModelV2.Status.POST_STATUS.name(),
                            new Date()));

            List<UserModel> friends = friendRepositoryImpl.getFriends(userId);

            friends.stream().forEach(t -> {
                messagingTemplate.convertAndSend("/room/user/" + t.getId(),
                        new ResultModelV2(ResultModelV2.Status.POST_STATUS.getStatus(),
                                MappedStatusModel.convertStatusModelToJson(postStatus),
                                ResultModelV2.Status.POST_STATUS.name(),
                                new Date()));
            });

        } catch (IOException | JSONException ex) {
            System.out.println(ex.getMessage() + "postStatus catch 1");
        }
    }

    @Override
    public void getStatuses(String json) {
        JSONObject jSONObject = new JSONObject(json);
        int userId = jSONObject.getInt("userid");
        List<StatusModel> statused = statusRepositoryImpl.getStatused();
        List<Map<String, Object>> convertStatusModelToJson = MappedStatusModel.convertStatusModelToJson(statused);
        messagingTemplate.convertAndSend("/room/user/" + userId,
                new ResultModelV2(ResultModelV2.Status.GET_STATUSES.getStatus(),
                        convertStatusModelToJson,
                        ResultModelV2.Status.GET_STATUSES.name(),
                        new Date()));

    }

    @Override
    public void loadMoreStatused(String json) {
         JSONObject jSONObject = new JSONObject(json);
        int userId = jSONObject.getInt("userid");
        int start = jSONObject.getInt("idStart");
        int count = jSONObject.getInt("count");
        List<StatusModel> statused = statusRepositoryImpl.loadMoreStatused(start,count);
        List<Map<String, Object>> convertStatusModelToJson = MappedStatusModel.convertStatusModelToJson(statused);
        messagingTemplate.convertAndSend("/room/user/" + userId,
                new ResultModelV2(ResultModelV2.Status.LOADMORE_STATUSES.getStatus(),
                        convertStatusModelToJson,
                        ResultModelV2.Status.LOADMORE_STATUSES.name(),
                        new Date()));
    }

}
