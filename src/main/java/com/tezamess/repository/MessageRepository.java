package com.tezamess.repository;

import com.tezamess.model.MessageModel;
import com.tezamess.model.TempMessageModel;
import java.util.List;

public interface MessageRepository {

    MessageModel saveMessage(MessageModel message);

    List<MessageModel> getMessagesUnread(int idUser);
    
    List<MessageModel> loadMessages(int idRoom,int start, int count);

    int updateStatusMessage(TempMessageModel tempMessageModel);

    TempMessageModel checkStatusMessage(MessageModel messageModel);
   
}
