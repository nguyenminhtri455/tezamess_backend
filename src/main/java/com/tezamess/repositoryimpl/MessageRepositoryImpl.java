package com.tezamess.repositoryimpl;

import com.tezamess.model.MessageModel;
import com.tezamess.model.RoomModel;
import com.tezamess.model.TempMessageModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.MessageRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        Query<TempMessageModel> query = ss.createQuery("FROM TempMessageModel t"
                + " WHERE t.idmember.id = :idmember"
                + " AND t.idRoom.id = :idRoom", TempMessageModel.class
        );

        query.setParameter("idmember", message.getUserid().getId());
        query.setParameter("idRoom", message.getRoomid().getId());

        List<TempMessageModel> resultList = query.getResultList();
        if (resultList.size() == 1) {
            resultList.get(0).setIdmessage(message);
            resultList.get(0).setStatusMessage(0);
            ss.saveOrUpdate(resultList.get(0));
        } else {
            Query<RoomModel> query1 = ss.createQuery("FROM RoomModel as r WHERE r.id = :id", RoomModel.class);
            query1.setParameter("id", message.getRoomid().getId());
            RoomModel room = query1.getSingleResult();
            room.getUserModelList().stream().forEach(t -> {
                TempMessageModel tempMessageModel = new TempMessageModel();

                if (Objects.equals(t.getId(), message.getUserid().getId())) {
                    tempMessageModel.setStatusMessage(0);
                } else {
                    tempMessageModel.setStatusMessage(-1);
                }
                tempMessageModel.setIdmessage(message);

                tempMessageModel.setIdmember(t);
                tempMessageModel.setIdRoom(message.getRoomid());
                ss.save(tempMessageModel);
            });
        }

        return message;
    }

    @Override
    public List<MessageModel> getMessagesUnread(int idUser) {
        Session ss = sessionFactory.getCurrentSession();
        Query query = ss.createQuery("FROM UserModel u WHERE u.id = :id", UserModel.class);
        query.setParameter("id", idUser);

        List<MessageModel> list = new ArrayList<>();
        UserModel resultList = (UserModel) query.getSingleResult();
        resultList.getRoomModelList().stream().forEach(t -> {
            Query query1 = ss.createQuery("FROM MessageModel m WHERE m.room.id = :roomid"
                    + " AND m.id BETWEEN"
                    + " (SELECT t.idmessage.id FROM TempMessageModel t"
                    + " WHERE t.idRoom.id = :roomid AND t.idmember.id = :memberid)"
                    + " AND (SELECT MAX(t.idmessage.id) FROM TempMessageModel t"
                    + " WHERE t.idRoom.id = :roomid)");
//            Query query1 = ss.createSQLQuery("SELECT * FROM message m WHERE m.groupid = :roomid"
//                    + " AND m.id BETWEEN"
//                    + " (SELECT t.idmessage FROM tempmessage t"
//                    + " WHERE t.roomid = :roomid AND t.idmember = :memberid)"
//                    + " AND (SELECT MAX(t.idmessage) FROM tempmessage t"
//                    + " WHERE t.roomid = :roomid)");          
            query1.setParameter("roomid", t.getId());
            query1.setParameter("memberid", idUser);
            List<MessageModel> messages = query1.getResultList();

            Query query2 = ss.createQuery("FROM TempMessageModel t"
                    + " WHERE t.idmember.id = :idmember"
                    + " AND t.idRoom.id = :idRoom", TempMessageModel.class
            );

            query2.setParameter("idmember", idUser);
            query2.setParameter("idRoom", t.getId());
            List<TempMessageModel> resultList1 = query2.getResultList();
            if (messages.size() == 1) {
                if (messages.get(0).getUserid().getId() != idUser && resultList1.get(0).getStatusMessage() == -1) {
                    list.addAll(messages);
                }
            } else {
                if (messages.size() > 0) {
                    if (resultList1.get(0).getStatusMessage() == -1) {
                        list.addAll(messages);
                    } else {
                        messages.remove(0);
                        list.addAll(messages);
                    }
                }
            }

        });

        return list;
    }

    @Override
    public int updateStatusMessage(TempMessageModel tempMessageModel) {
        Session ss = sessionFactory.getCurrentSession();
        Query<TempMessageModel> query = ss.createQuery("FROM TempMessageModel t"
                + " WHERE t.idmember.id = :idmember"
                + " AND t.idRoom.id = :idRoom", TempMessageModel.class
        );

        query.setParameter("idmember", tempMessageModel.getIdmember().getId());
        query.setParameter("idRoom", tempMessageModel.getIdRoom().getId());

        TempMessageModel tempMessage = query.getSingleResult();
        if (tempMessage != null) {

            if (tempMessage.getStatusMessage() == 2) {
                return 2;
            }

            tempMessage.setIdmessage(tempMessageModel.getIdmessage());
            tempMessage.setStatusMessage(tempMessageModel.getStatusMessage());
            ss.saveOrUpdate(tempMessage);
        }
        return tempMessageModel.getStatusMessage();
    }

    @Override
    public TempMessageModel checkStatusMessage(MessageModel messageModel) {
        Session ss = sessionFactory.getCurrentSession();
        Query<TempMessageModel> query = ss.createQuery("FROM TempMessageModel t"
                + " WHERE t.idmessage.id = :idmessage"
                + " AND t.idRoom.id = :idRoom", TempMessageModel.class
        );

        query.setParameter("idmessage", messageModel.getId());
        query.setParameter("idRoom", messageModel.getRoomid().getId());

        List<TempMessageModel> resultList = query.getResultList();
        if (resultList.size() > 0) {
            for (TempMessageModel t : resultList) {
                if (Objects.equals(t.getIdmember().getId(), messageModel.getUserid().getId())) {
                    continue;
                }
                if (t.getStatusMessage() == 1 || t.getStatusMessage() == 2) {
                    System.out.println(t.getIdmessage().getBody());
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public List<MessageModel> loadMessages(int idRoom, int start, int count) {
        Session ss = sessionFactory.getCurrentSession();
        Query<MessageModel> query = ss.createQuery("FROM MessageModel m"
                + " WHERE m.room.id = :idRoom"
                + " ORDER BY m.id DESC", MessageModel.class);
        query.setParameter("idRoom", idRoom);
        query.setFirstResult(start);
        query.setMaxResults(count);
        List<MessageModel> resultList = query.getResultList();
        return resultList;
    }
}
