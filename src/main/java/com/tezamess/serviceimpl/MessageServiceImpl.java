package com.tezamess.serviceimpl;

import com.tezamess.model.MessageModel;
import com.tezamess.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MessageServiceImpl implements MessageService{

    @Autowired
    private MessageServiceImpl messageServiceImpl;
    
    @Override
    public void saveMessage(MessageModel message) {
        messageServiceImpl.saveMessage(message);
    }
    
}
