package com.tezamess.service;

import java.util.List;
import java.util.Map;


public interface MessageService {
    String saveMessage(String json);
    List<Map<String, Object>> getMessageaUnread(int id);
}
