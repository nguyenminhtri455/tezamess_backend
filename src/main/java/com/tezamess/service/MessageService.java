package com.tezamess.service;

import java.util.List;
import java.util.Map;

public interface MessageService {

    String saveMessage(String json);

    List<Map<String, Object>> getMessageaUnread(int id);

    List<Map<String, Object>> loadMessages(int idRoom, int start, int count);

    String updateStatusMessage(String json);

    String checkStatusMessage(String json);
    
    
}
