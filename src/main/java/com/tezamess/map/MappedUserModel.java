package com.tezamess.map;

import com.tezamess.model.UserModel;
import java.util.HashMap;
import java.util.Map;


public class MappedUserModel {
    
    public static Map<String,Object> convertToMap(UserModel user){
        Map<String,Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("phone", user.getPhone());
        map.put("name", user.getName());
        map.put("password", user.getPassword());
        map.put("birthday", user.getBirthday());
        map.put("gender", user.getGender());
        map.put("urlavatar", user.getUrlavatar());
        map.put("online", user.getOnline());
        map.put("lastactive", user.getLastactive());   
        return map;
    }
    
    public static Map<String,Object> convertToMapBy4Record(UserModel user){
        Map<String,Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("phone", user.getPhone());
        map.put("name", user.getName());   
        map.put("urlavatar", user.getUrlavatar());    
        return map;
    }
}
