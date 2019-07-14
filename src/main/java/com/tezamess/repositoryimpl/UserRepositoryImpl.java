package com.tezamess.repositoryimpl;

import com.tezamess.model.MessageModel;
import com.tezamess.model.RoomModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.UserRepository;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<UserModel> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM UserModel", UserModel.class).getResultList();
    }

    @Override
    public UserModel login(UserModel userModel) {
        String passwordEncryption = Base64.getEncoder().encodeToString(userModel.getPassword().getBytes());
        userModel.setPassword(passwordEncryption);
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM UserModel u WHERE u.phone = :phone AND u.password = :password ", UserModel.class);
        query.setParameter("phone", userModel.getPhone());
        query.setParameter("password", userModel.getPassword());
        List<UserModel> resultList = query.getResultList();
        if (resultList.size() > 0) {
            int size = resultList.get(0).getRoomModelList().size();
            for (RoomModel room : resultList.get(0).getRoomModelList()) {
                Query query1 = session.createQuery("FROM MessageModel as m WHERE m.room.id = :idRoom ORDER BY m.id DESC");
                query1.setFirstResult(0);
                query1.setMaxResults(50);
                query1.setParameter("idRoom", room.getId());
                List<MessageModel> resultList1 = query1.getResultList();
                room.setMessageList(new HashSet<>(resultList1));
            }
            return resultList.get(0);
        }
        return null;
    }

    @Override
    public UserModel register(UserModel userModel) {
        userModel.setBirthday(new Date());
        userModel.setLastactive(new Date());
        userModel.setGender(false);
        String passwordEncryption = Base64.getEncoder().encodeToString(userModel.getPassword().getBytes());
        userModel.setPassword(passwordEncryption);
        if (userExists(userModel)) {
            return null;
        }
        Session session = sessionFactory.getCurrentSession();
        session.save(userModel);
        return userModel;
    }

    @Override
    public UserModel updateUser(UserModel user) {
        Session session = sessionFactory.getCurrentSession();
        UserModel userModel = findUserByPhone(user.getPhone());
        if (userModel != null) {
            userModel.setName(user.getName());
            userModel.setGender(user.getGender());
            userModel.setBirthday(user.getBirthday());
            if (user.getUrlavatar() != null) {
                userModel.setUrlavatar(user.getUrlavatar());
            }
            return userModel;
        }
        return null;
    }

    @Override
    public UserModel changePassword(UserModel user) {
        Session session = sessionFactory.getCurrentSession();
        UserModel userModel = findUserByPhone(user.getPhone());
        if (userModel != null) {
            String passwordEncryption = Base64.getEncoder().encodeToString(user.getPassword().getBytes());
            userModel.setPassword(passwordEncryption);
            return userModel;
        }
        return null;
    }

    @Override
    public UserModel updateEmail(UserModel user) {
        Session session = sessionFactory.getCurrentSession();
        UserModel userModel = findUserByPhone(user.getPhone());
        if (userModel != null) {          
            userModel.setEmail(user.getEmail());
            return userModel;
        }
        return null;
    }

    @Override
    public Boolean userExists(UserModel user) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM UserModel WHERE phone = :phone");
        query.setParameter("phone", user.getPhone());
        List<UserModel> users = query.getResultList();
        if (users != null && users.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public UserModel findUserByPhone(String phone) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM UserModel WHERE phone = :phone");
        query.setParameter("phone", phone);
        List<UserModel> users = query.getResultList();
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    @Override
    public UserModel findUserById(int id) {
        Session session = sessionFactory.getCurrentSession();
        UserModel user = session.get(UserModel.class, id);
        if (user != null) {
            return user;
        }
        return null;
    }

    @Override
    public List<Object[]> checkUserUsingApp(int id, List<Object> listPhone) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery(
                "SELECT m.id as mid, m.phone as mphone, m.name as mname, m.urlavatar as murl, f.id as fid "
                + "FROM (SELECT u.id, u.phone, u.name, u.urlavatar FROM member as u "
                + "WHERE u.phone IN :phones) as m LEFT JOIN friend as f "
                + "ON (f.useridrequest = :id AND f.useridfriend = m.id) "
                + "OR (f.useridfriend = :id AND f.useridrequest = m.id)"
        );
//        Query query = session.createQuery(
//                "SELECT m.id, m.phone, m.name, m.urlavatar, f.id "
//                + "FROM (SELECT u.id, u.phone, u.name, u.urlavatar FROM UserModel as u "
//                + "WHERE u.phone IN :phones) as m LEFT JOIN FriendModel as f "
//                + "WHERE (f.useridrequest.id = :id AND f.useridfriend.id = m.id) "
//                + "OR (f.useridfriend.id = :id AND f.useridrequest.id = m.id)"
//        );
        query.setParameterList("phones", listPhone);
        query.setParameter("id", id);
        List<Object[]> users = query.getResultList();
        if (users != null && users.size() > 0) {
            return users;
        }
        return null;
    }

    @Override
    public UserModel findUserByIdWithRoom(int id) {
        Session session = sessionFactory.getCurrentSession();
        UserModel user = session.get(UserModel.class, id);
        if (user != null) {
            user.getRoomModelList().size();
            return user;
        }
        return null;
    }

    @Override
    public void updateLastActive(int id) {
        Session session = sessionFactory.getCurrentSession();
        UserModel user = session.get(UserModel.class, id);
        user.setLastactive(new Date());
        session.saveOrUpdate(user);
    }
}
