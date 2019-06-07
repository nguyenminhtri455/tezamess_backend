package com.tezamess.repository;

import com.tezamess.model.MessageModel;

public interface MessageRepository {
    MessageModel saveMessage(MessageModel message);
}
