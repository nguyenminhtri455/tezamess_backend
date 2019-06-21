package com.tezamess.repositoryimpl;

import com.tezamess.model.MessageModel;
import com.tezamess.model.RoomModel;
import com.tezamess.model.TempMessageModel;
import com.tezamess.repository.MessageRepository;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
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

        Query<RoomModel> query = ss.createQuery("FROM RoomModel as r WHERE r.id = :id", RoomModel.class);
        query.setParameter("id", message.getRoomid().getId());
        RoomModel room = query.getSingleResult();
        room.getUserModelList().stream().forEach(t -> {
            System.out.println(t.getName());
            TempMessageModel tempMessageModel = new TempMessageModel();
            tempMessageModel.setStatusmessage(0);
            tempMessageModel.setIdmessage(message);
            tempMessageModel.setIdmember(t);
            ss.save(tempMessageModel);
        });

        return message;
    }

    @Override
    public List<Object[]> getMessagesUnread(int idUser) {

        Session ss = sessionFactory.getCurrentSession();
        Query query = ss.createQuery("SELECT t.idmember.id FROM TempMessageModel t JOIN t.idmember"
                + " WHERE t.idmember.id = :id");
        query.setParameter("id", idUser);

        List<Object[]> resultList = query.getResultList();
        resultList.stream().forEach(t -> {
            System.out.println(t[0]);
            System.out.println(t[1]);
            System.out.println(t[2]);
            System.out.println(t[3]);
            System.out.println(t[4]);
            System.out.println(t[5]);
            System.out.println("---------------------------------");

        });
        return null;
    }
}
