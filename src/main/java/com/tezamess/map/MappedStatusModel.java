package com.tezamess.map;

import com.tezamess.model.StatusModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappedStatusModel {

    public static Map<String, Object> convertStatusModelToJson(StatusModel statusModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", statusModel.getId());
        map.put("createdate", statusModel.getCreatedate().getTime());
        map.put("body", statusModel.getBody());
        map.put("user", MappedUserModel.convertToUser4Record(statusModel.getUserPostStatus()));
        if (!statusModel.getListMediaModel().isEmpty()) {
            map.put("images", statusModel.getListMediaModel());
        }
        return map;
    }

    public static List<Map<String, Object>> convertStatusModelToJson(List<StatusModel> statusModel) {
        List<Map<String, Object>> list = new ArrayList();
        statusModel.forEach(s -> {
            list.add(convertStatusModelToJson(s));
        });
        return list;
    }
}
