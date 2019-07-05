package com.tezamess.repositoryimpl;

import com.tezamess.model.FriendModel;
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
        FriendModel friendModel = new FriendModel();
        friendModel.setUseridrequest(new UserModel(idfriend));
        friendModel.setUseridfriend(new UserModel(id));
        session.save(friendModel);
    }
}
