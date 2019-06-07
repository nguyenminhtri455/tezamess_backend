package com.tezamess.repositoryimpl;

import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.RoomRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class RoomRepositoryImpl implements RoomRepository{
    
    @Autowired
    SessionFactory sessionFactory;
    
    @Override
    public RoomModel createRoom(String name, UserModel userModel) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel roomModel = new RoomModel();
        roomModel.setName(name);
        roomModel.setCreator(userModel);
        session.save(roomModel);
        return roomModel;
    }
    
     @Override
    public void deleteRoom(int id) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = session.get(RoomModel.class, id);
        if(room != null){
            session.delete(room);
        }
    }

    @Override
    public RoomModel findRoom(int id) {
        Session session = sessionFactory.getCurrentSession();
        RoomModel room = session.get(RoomModel.class,id);
        if(room != null){
            return room;
        }
        return null;
    }
}
