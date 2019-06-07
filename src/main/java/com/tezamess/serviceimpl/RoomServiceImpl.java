package com.tezamess.serviceimpl;

import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.repositoryimpl.RoomRepositoryImpl;
import com.tezamess.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService{
    
    @Autowired
    private RoomRepositoryImpl repositoryImpl;

    @Override
    public RoomModel createRoom(String name, UserModel userModel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteRoom(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RoomModel findRoom(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
