package com.tezamess.repository;

import com.tezamess.model.MessageModel;
import com.tezamess.model.TempMessageModel;
import java.util.List;

public interface MessageRepository {

    MessageModel saveMessage(MessageModel message);

    List<MessageModel> getMessagesUnread(int idUser);

    List<MessageModel> loadMessages(int idRoom, int start, int count);

    int updateStatusMessage(TempMessageModel tempMessageModel);

    TempMessageModel checkStatusMessage(MessageModel messageModel);

    List<TempMessageModel> checkDetailStatusMessage(MessageModel messageModel);

    //kiem tra tin nhan tu tin nhan hien tai trong tempmessage va tin nhan yeu cau kiem tra trong phong 
    //co khoang tin tin nao la do user gui hay khong (neu co thi user da xem nguoc la chi la da nhan)
    int checkStatusMessageSeenOrRecevied(int idMessageCurrent, int idMessageCheck, int idUser, int idRoom);

}
