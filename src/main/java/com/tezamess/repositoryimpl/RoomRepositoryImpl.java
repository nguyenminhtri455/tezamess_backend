package com.tezamess.repositoryimpl;

import com.tezamess.model.RoomModel;
import com.tezamess.model.TypeRoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.RoomRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public RoomModel createRoom(String name, int idCreateUser, int... ids) {
        Session session = sessionFactory.getCurrentSession();

        RoomModel room = new RoomModel();
        if (ids.length > 1) {
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
        List<Integer> l = new ArrayList<Integer>();
        l.add(idCreateUser);
        for (int i : ids) {
            l.add(i);
        }

        query.setParameterList("ids", l);

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
        if (room != null) {
            return room;
        }
        return null;
    }
}
