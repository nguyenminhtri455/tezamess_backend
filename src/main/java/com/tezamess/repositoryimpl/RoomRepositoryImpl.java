package com.tezamess.repositoryimpl;

import com.tezamess.model.MessageModel;
import com.tezamess.model.ParticipationModel;
import com.tezamess.model.RoomModel;
import com.tezamess.model.TempMessageModel;
import com.tezamess.model.TypeRoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.RoomRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class RoomRepositoryImpl implements RoomRepository {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public RoomModel createRoom(String name, int idCreateUser, List<Integer> listId) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = new RoomModel();
        if (!name.isEmpty()) {
            room.setName(name);
            room.setTypeRoomModel(new TypeRoomModel("G"));
        } else {
            room.setTypeRoomModel(new TypeRoomModel("D"));
        }
        if (idCreateUser != -1) {
            room.setCreator(new UserModel(idCreateUser));
        }

        session.save(room);

        Query query = session.createQuery("From UserModel WHERE id IN :ids", UserModel.class);

        query.setParameterList("ids", listId);
        List<UserModel> resultList = query.getResultList();

        List<ParticipationModel> participationModels = new ArrayList();
        resultList.stream().forEach(t -> {

            ParticipationModel participationModel = new ParticipationModel();
            participationModel.setRoom(room);
            participationModel.setUser(t);
            participationModels.add(participationModel);
            session.save(participationModel);
        });
        Set<ParticipationModel> set = new HashSet<>(participationModels);
        room.setParticipationModels(set);
        return room;
    }

    @Override
    public RoomModel createRoom(String name, String avatar, int idCreateUser, List<Integer> listId) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = new RoomModel();
        if (!name.isEmpty()) {
            room.setName(name);
            room.setTypeRoomModel(new TypeRoomModel("G"));
        } else {
            room.setTypeRoomModel(new TypeRoomModel("D"));
        }
        if (idCreateUser != -1) {
            room.setCreator(new UserModel(idCreateUser));
        }
        room.setAvatar(avatar);
        session.save(room);

        Query query = session.createQuery("From UserModel WHERE id IN :ids", UserModel.class);

        query.setParameterList("ids", listId);
        List<UserModel> resultList = query.getResultList();

        List<ParticipationModel> participationModels = new ArrayList();
        resultList.stream().forEach(t -> {

            ParticipationModel participationModel = new ParticipationModel();
            participationModel.setRoom(room);
            participationModel.setUser(t);
            participationModels.add(participationModel);
            session.save(participationModel);
        });
        Set<ParticipationModel> set = new HashSet<>(participationModels);
        room.setParticipationModels(set);
        return room;
    }

    @Override
    public void deleteRoom(int id) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = session.get(RoomModel.class, id);
        if (room != null) {
            session.delete(room);
        }
    }

    @Override
    public RoomModel findRoom(int id) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = session.get(RoomModel.class, id);
        return room;
    }

    @Override
    public RoomModel findRoom(List<Integer> listId) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("SELECT * FROM participation as p"
                + " WHERE p.userid IN :ids"
                + " GROUP BY p.roomid"
                + " HAVING COUNT(*) = :size");
        query.setParameterList("ids", listId);
        query.setParameter("size", listId.size());
        List<Object[]> resultList = query.getResultList();

        for (Object[] o : resultList) {
            RoomModel room = session.get(RoomModel.class, (int) o[1]);
            List<Integer> collect = room.getParticipationModels().stream()
                    .map((participation) -> participation.getUser().getId())
                    .collect(Collectors.toList());
            collect.removeAll(listId);
            if (collect.isEmpty()) {
                return room;
            }
        };
        return null;
    }

    //khong con su dung
    @Override
    public void changeStatusReceivedRoom(int id, int idRoom) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM ParticipationModel p"
                + " WHERE p.room.id = :idRoom"
                + " AND p.user.id = :id", ParticipationModel.class);
        query.setParameter("idRoom", idRoom);
        query.setParameter("id", id);
        ParticipationModel singleResult = (ParticipationModel) query.getSingleResult();
//        singleResult.setStatus(1);
    }

    //khong con su dung
    @Override
    public List<RoomModel> getRoomNotReceived(int id) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("SELECT p.room FROM ParticipationModel p"
                + " WHERE p.user.id = :id"
                + " AND p.status = :status", RoomModel.class);
        query.setParameter("id", id);
        query.setParameter("status", 0);
        List<RoomModel> rooms = query.getResultList();
        return rooms;
    }

    @Override
    public RoomModel inviteMember(int idRoom, List<Integer> ids) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = session.get(RoomModel.class, idRoom);
        ids.stream().forEach(t -> {
            ParticipationModel participationModel = new ParticipationModel();
            participationModel.setUser(session.get(UserModel.class, t));
            participationModel.setRoom(room);
            session.save(participationModel);
            room.getParticipationModels().add(participationModel);
            Query query = session.createQuery("FROM MessageModel m"
                    + " WHERE m.id = (SELECT MIN(m.id)"
                    + " FROM MessageModel m"
                    + " WHERE m.room.id = :roomId)", MessageModel.class);

            query.setParameter("roomId", idRoom);

            List<MessageModel> resultList = query.getResultList();
            if (resultList.size() > 0) {
                MessageModel message = resultList.get(0);
                TempMessageModel tempMessageModel = new TempMessageModel();
                tempMessageModel.setStatusMessage(-1);
                tempMessageModel.setIdRoom(room);
                tempMessageModel.setIdmember(new UserModel(t));
                tempMessageModel.setIdmessage(message);

                session.save(tempMessageModel);
            }

        });
        RoomModel newRoom = session.get(RoomModel.class, idRoom);
        return newRoom;
    }

    @Override
    public RoomModel leaveRoom(int idRoom, int idUser) {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("DELETE ParticipationModel p"
                + " WHERE p.room.id = :idRoom"
                + " AND p.user.id = :idUser");

        query.setParameter("idRoom", idRoom);
        query.setParameter("idUser", idUser);
        query.executeUpdate();

        Query query1 = session.createQuery("DELETE TempMessageModel t"
                + " WHERE t.idRoom.id = :idRoom"
                + " AND t.idmember.id = :idUser");

        query1.setParameter("idRoom", idRoom);
        query1.setParameter("idUser", idUser);
        query1.executeUpdate();

        RoomModel room = session.get(RoomModel.class, idRoom);
        if (room.getCreator().getId() == idUser) {
            for (ParticipationModel p : room.getParticipationModels()) {
                room.setCreator(p.getUser());
                session.saveOrUpdate(room);
                break;
            }
        }
        return room;
    }

    @Override
    public RoomModel updateRoom(RoomModel roomModel) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = session.get(RoomModel.class, roomModel.getId());
        if (room != null) {
            if (roomModel.getName() != null) {
                room.setName(roomModel.getName());
            }
            if (roomModel.getAvatar() != null) {
                room.setAvatar(roomModel.getAvatar());
            }
        }
        return room;
    }

}
