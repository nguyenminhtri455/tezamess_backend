package com.tezamess.repositoryimpl;

import com.tezamess.model.ParticipationModel;
import com.tezamess.model.RoomModel;
import com.tezamess.model.TypeRoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.RoomRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
//        Session session = sessionFactory.getCurrentSession();
//        RoomModel room = new RoomModel();
//        if (!name.isEmpty()) {
//            room.setName(name);
//            room.setTypeRoomModel(new TypeRoomModel("G"));
//        } else {
//            room.setTypeRoomModel(new TypeRoomModel("D"));
//        }
//        if (idCreateUser != -1) {
//            room.setCreator(new UserModel(idCreateUser));
//        }
//
//        session.save(room);
//
//        Query query = session.createQuery("From UserModel WHERE id IN :ids", UserModel.class);
//
//        query.setParameterList("ids", listId);
//        List<UserModel> resultList = query.getResultList();
//        Set<UserModel> set = new HashSet<UserModel>(resultList);
//        room.setUserModelList(set);
//        return room;

//-------------------------------------------------
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
            if (t.getId() == idCreateUser) {
                participationModel.setStatus(1);
            } else {
                participationModel.setStatus(0);
            }
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
//        Session session = sessionFactory.getCurrentSession();
//        Query query = session.createSQLQuery("SELECT * FROM participation as p"
//                + " WHERE p.userid IN :ids"
//                + " GROUP BY p.groupid"
//                + " HAVING COUNT(*) = :size");
//        query.setParameterList("ids", listId);
//        query.setParameter("size", listId.size());
//        List<Object[]> resultList = query.getResultList();
//        for (Object[] o : resultList) {
//            RoomModel room = session.get(RoomModel.class, (int) o[0]);
//            List<Integer> collect = room.getUserModelList().stream()
//                    .map(UserModel::getId)
//                    .collect(Collectors.toList());
//            collect.removeAll(listId);
//            if (collect.size() == 0) {
//                return room;
//            }
//        };
//        return null;

//-------------------------------------------------
   
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("SELECT * FROM participation as p"
                + " WHERE p.userid IN :ids"
                + " GROUP BY p.groupid"
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

    @Override
    public void changeStatusReceivedRoom(int id, int idRoom) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM ParticipationModel p"
                + " WHERE p.room.id = :idRoom"
                + " AND p.user.id = :id", ParticipationModel.class);
        query.setParameter("idRoom", idRoom);
        query.setParameter("id", id);
        ParticipationModel singleResult = (ParticipationModel) query.getSingleResult();
        singleResult.setStatus(1);
    }

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
}
