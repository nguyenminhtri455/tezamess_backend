package com.tezamess.repository;

import com.tezamess.model.MessageModel;
import java.util.List;

public interface MessageRepository {
    MessageModel saveMessage(MessageModel message);
    List<Object[]> getMessagesUnread(int idUser);
}
