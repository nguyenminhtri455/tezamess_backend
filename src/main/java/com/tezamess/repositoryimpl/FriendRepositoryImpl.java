package com.tezamess.repositoryimpl;

import com.tezamess.model.FriendModel;
import com.tezamess.model.TempFriendModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.FriendRepository;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class FriendRepositoryImpl implements FriendRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<UserModel> getFriends(int idUser) {
        List<UserModel> listUser = new ArrayList<>();
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery("FROM FriendModel as f "
                + "WHERE f.useridrequest.id = :id "
                + "OR f.useridfriend.id = :id");
        query.setParameter("id", idUser);

        List<FriendModel> friends = query.getResultList();

        friends.stream().forEach(t -> {
            if (idUser != t.getUseridfriend().getId()) {
                listUser.add(session.get(UserModel.class, t.getUseridfriend().getId()));
            }
            if (idUser != t.getUseridrequest().getId()) {
                listUser.add(session.get(UserModel.class, t.getUseridrequest().getId()));
            }
        });
        return listUser;
    }

    @Override
    public void addFriend(int id, int idfriend) {
        Session session = sessionFactory.getCurrentSession();
        //luu ket ban
        FriendModel friendModel = new FriendModel();
        friendModel.setUseridrequest(new UserModel(idfriend));
        friendModel.setUseridfriend(new UserModel(id));
        session.save(friendModel);

        //thay doi trang thai ket ban
        Query query = session.createQuery("FROM TempFriendModel t"
                + " WHERE t.userFriend.id = :idUserFriend"
                + " AND t.userRequest.id = :idUserRequest", TempFriendModel.class);
        query.setParameter("idUserRequest", idfriend);
        query.setParameter("idUserFriend", id);
        TempFriendModel singleResult = (TempFriendModel) query.getSingleResult();
        singleResult.setStatus(1);
        session.saveOrUpdate(singleResult);
    }
    
    @Override
    public void disAgreeAddFriend(int id, int idfriend) {
        Session session = sessionFactory.getCurrentSession();
        //thay doi trang thai ket ban
        Query query = session.createQuery("FROM TempFriendModel t"
                + " WHERE t.userFriend.id = :idUserFriend"
                + " AND t.userRequest.id = :idUserRequest", TempFriendModel.class);
        query.setParameter("idUserRequest", idfriend);
        query.setParameter("idUserFriend", id);
        TempFriendModel singleResult = (TempFriendModel) query.getSingleResult();
        singleResult.setStatus(-1);
        session.saveOrUpdate(singleResult);
    }

    @Override
    public void requestAddFriend(int idUserRequest, int idUserFriend) {
        Session session = sessionFactory.getCurrentSession();
        TempFriendModel tempFriendModel = new TempFriendModel();
        tempFriendModel.setUserRequest(new UserModel(idUserRequest));
        tempFriendModel.setUserFriend(new UserModel(idUserFriend));
        tempFriendModel.setStatus(0);
        session.save(tempFriendModel);

    }

    @Override
    public List<UserModel> getRequestAddFriend(int idUserFriend) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("SELECT t.userRequest"
                + " FROM TempFriendModel t"
                + " WHERE t.userFriend.id = :idUserFriend"
                + " AND t.status = :status", UserModel.class);
        query.setParameter("idUserFriend", idUserFriend);
        query.setParameter("status", 0);
        List<UserModel> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<UserModel> getResponseAddFriend(int idUserRequest) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("SELECT t.userFriend"
                + " FROM TempFriendModel t"
                + " WHERE t.userRequest.id = :idUserRequest"
                + " AND t.status = :status", UserModel.class);
        query.setParameter("idUserRequest", idUserRequest);
        query.setParameter("status", 1);
        List<UserModel> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<UserModel> getDisAgreeResponseAddFriend(int idUserRequest) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("SELECT t.userFriend"
                + " FROM TempFriendModel t"
                + " WHERE t.userRequest.id = :idUserRequest"
                + " AND t.status = :status", UserModel.class);
        query.setParameter("idUserRequest", idUserRequest);
        query.setParameter("status", -1);
        List<UserModel> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public void removeTempFriend(int id, int status) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("DELETE FROM TempFriendModel t"
                + " WHERE t.userRequest.id = :idUserRequest"
                + " AND t.status = :status");
        query.setParameter("idUserRequest", id);
        query.setParameter("status", status);
        query.executeUpdate();
    }

    @Override
    public List<UserModel> getUserSentRequestAddFriend(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("SELECT t.userFriend"
                + " FROM TempFriendModel t"
                + " WHERE t.userRequest.id = :idUserRequest"
                + " AND t.status = :status", UserModel.class);
        query.setParameter("idUserRequest", id);
        query.setParameter("status", 0);
        List<UserModel> resultList = query.getResultList();
        return resultList;
    }
}
