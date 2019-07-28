package com.tezamess.repositoryimpl;

import com.tezamess.model.FriendModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.FriendRepository;
import java.util.ArrayList;
import java.util.Arrays;
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
                + "WHERE f.status IN :status AND (f.userRequest.id = :id "
                + "OR f.userFriend.id = :id)");

        query.setParameterList("status", Arrays.asList(1, 2));
        query.setParameter("id", idUser);

        List<FriendModel> friends = query.getResultList();

        friends.stream().forEach(t -> {
            if (idUser != t.getUserFriend().getId()) {
                listUser.add(session.get(UserModel.class, t.getUserFriend().getId()));
            }
            if (idUser != t.getUserRequest().getId()) {
                listUser.add(session.get(UserModel.class, t.getUserRequest().getId()));
            }
        });
        return listUser;
    }

    @Override
    public void addFriend(int id, int idfriend) {
        Session session = sessionFactory.getCurrentSession();
        //luu ket ban
//        FriendModel friendModel = new FriendModel();
//        friendModel.setUserRequest(new UserModel(idfriend));
//        friendModel.setUserFriend(new UserModel(id));
//        friendModel.setStatus(1);
//        session.save(friendModel);

        //thay doi trang thai ket ban
        Query query = session.createQuery("FROM FriendModel t"
                + " WHERE t.userFriend.id = :idUserFriend"
                + " AND t.userRequest.id = :idUserRequest", FriendModel.class);
        query.setParameter("idUserRequest", idfriend);
        query.setParameter("idUserFriend", id);
        FriendModel singleResult = (FriendModel) query.getSingleResult();
        singleResult.setStatus(1);
        session.saveOrUpdate(singleResult);
    }

    @Override
    public void addFriendAdmin(int id, int idfriend) {
        Session session = sessionFactory.getCurrentSession();
        //luu ket ban
        FriendModel friendModel = new FriendModel();
        friendModel.setUserRequest(new UserModel(idfriend));
        friendModel.setUserFriend(new UserModel(id));
        friendModel.setStatus(2);
        session.save(friendModel);
    }

    @Override
    public void disAgreeAddFriend(int id, int idfriend) {
        Session session = sessionFactory.getCurrentSession();
        //thay doi trang thai ket ban
        Query query = session.createQuery("FROM FriendModel t"
                + " WHERE t.userFriend.id = :idUserFriend"
                + " AND t.userRequest.id = :idUserRequest", FriendModel.class);
        query.setParameter("idUserRequest", idfriend);
        query.setParameter("idUserFriend", id);
        FriendModel singleResult = (FriendModel) query.getSingleResult();
        singleResult.setStatus(-1);
        session.saveOrUpdate(singleResult);
    }

    @Override
    public void requestAddFriend(int idUserRequest, int idUserFriend) {
        Session session = sessionFactory.getCurrentSession();
        FriendModel friendModel = new FriendModel();
        friendModel.setUserRequest(new UserModel(idUserRequest));
        friendModel.setUserFriend(new UserModel(idUserFriend));
        friendModel.setStatus(0);
        session.save(friendModel);
    }

    //request cua nguoi yeu cau ket ban
    @Override
    public List<UserModel> getUserSentRequestAddFriend(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("SELECT t.userFriend"
                + " FROM FriendModel t"
                + " WHERE t.userRequest.id = :idUserRequest"
                + " AND t.status = :status", UserModel.class);
        query.setParameter("idUserRequest", id);
        query.setParameter("status", 0);
        List<UserModel> resultList = query.getResultList();
        return resultList;
    }

    //request cua nguoi duoc yeu cau ket ban
    @Override
    public List<UserModel> getRequestAddFriend(int idUserFriend) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("SELECT t.userRequest"
                + " FROM FriendModel t"
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
                + " FROM FriendModel t"
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
                + " FROM FriendModel t"
                + " WHERE t.userRequest.id = :idUserRequest"
                + " AND t.status = :status", UserModel.class);
        query.setParameter("idUserRequest", idUserRequest);
        query.setParameter("status", -1);
        List<UserModel> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public void updateOrDeleteStatusAddFriend(int id, int status) {
        Session session = sessionFactory.getCurrentSession();
        if (status == -1) {
            Query query = session.createQuery("DELETE FROM FriendModel t"
                    + " WHERE t.userRequest.id = :idUserRequest"
                    + " AND t.status = :status");
            query.setParameter("idUserRequest", id);
            query.setParameter("status", status);
            query.executeUpdate();
        }
        if (status == 1) {
            Query query = session.createQuery("FROM FriendModel t"
                    + " WHERE t.userRequest.id = :idUserRequest"
                    + " AND t.status = :status", FriendModel.class);
            query.setParameter("idUserRequest", id);
            query.setParameter("status", status);
            FriendModel singleResult = (FriendModel) query.getSingleResult();
            singleResult.setStatus(2);
        }

    }

    @Override
    public void unFriend(int id, int idfriend) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("DELETE FROM FriendModel f"
                + " WHERE (f.userRequest.id = :userRequest"
                + " AND f.userFriend.id = :userFriend) OR "
                + "(f.userFriend.id = :userRequest"
                + " AND f.userRequest.id = :userFriend)");
        query.setParameter("userRequest", id);
        query.setParameter("userFriend", idfriend);
        query.executeUpdate();
    }
}
