package com.tezamess.repositoryimpl;

import com.tezamess.model.MessageModel;
import com.tezamess.repository.MessageRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class MessageRepositoryImpl implements MessageRepository {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public MessageModel saveMessage(MessageModel message) {
        Session ss = sessionFactory.getCurrentSession();
        ss.save(message);
        return message;
    }
}
