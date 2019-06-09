package com.tezamess.repository;

import com.tezamess.model.MessageModel;

public interface MessageRepository {
    void saveMessage(MessageModel message);
}
