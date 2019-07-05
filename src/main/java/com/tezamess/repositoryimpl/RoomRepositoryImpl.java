package com.tezamess.repositoryimpl;

import com.tezamess.model.RoomModel;
import com.tezamess.model.TypeRoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.RoomRepository;
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
        System.out.println(listId.size());
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = new RoomModel();
        if (listId.size() > 2) {
            room.setTypeRoomModel(new TypeRoomModel("G"));
        } else {
            room.setTypeRoomModel(new TypeRoomModel("D"));
        }
        if (name != null) {
            room.setName(name);
        }
        if (idCreateUser != -1) {
//            UserModel get = session.get(UserModel.class, idCreateUser);
            room.setCreator(new UserModel(idCreateUser));
        }

        session.save(room);

        Query query = session.createQuery("From UserModel WHERE id IN :ids", UserModel.class);

        query.setParameterList("ids", listId);
        List<UserModel> resultList = query.getResultList();
        Set<UserModel> set = new HashSet<UserModel>(resultList);
        room.setUserModelList(set);
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

//        Query query = session.createSQLQuery("SELECT * FROM "
//                + " participation WHERE groupid IN (SELECT DISTINCT p.groupid FROM room,member,participation as p"
//                + " WHERE p.userid = member.id AND p.groupid = room.id AND p.userid IN :ids)"
//                + " GROUP BY groupid"
//                + " HAVING COUNT(*) = :size");
//        Query query = session.createQuery("FROM RoomModel as d"
//                + " WHERE d.id IN (SELECT DISTINCT r.id FROM RoomModel r JOIN r.userModelList e"
//                + " WHERE e.id IN :ids)"
//                + " GROUP BY d.id", RoomModel.class);
//        Query query = session.createQuery("FROM RoomModel r JOIN r.userModelList e"
//                + " WHERE e.id IN :ids"
//                + " GROUP BY r");
        Query query = session.createSQLQuery("SELECT * FROM participation as p"
                + " WHERE p.userid IN :ids"
                + " GROUP BY p.groupid"
                + " HAVING COUNT(*) = :size");
        query.setParameterList("ids", listId);
        query.setParameter("size", listId.size());
        List<Object[]> resultList = query.getResultList();
        for (Object[] o : resultList) {
            RoomModel room = session.get(RoomModel.class, (int) o[0]);
            List<Integer> collect = room.getUserModelList().stream()
                    .map(UserModel::getId)
                    .collect(Collectors.toList());
            collect.removeAll(listId);
            if (collect.size() == 0) {
                return room;
            }
        };
        return null;
    }
}
