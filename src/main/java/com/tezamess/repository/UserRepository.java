package com.tezamess.repository;

import com.tezamess.model.UserModel;
import java.util.List;

public interface UserRepository {

    List<UserModel> findAll();

    UserModel login(UserModel user);

    UserModel register(UserModel user);

    UserModel updateUser(UserModel user);

    UserModel changePassword(UserModel user);

    UserModel findUserById(int id);

    UserModel findUserByIdWithRoom(int id);

    UserModel findUserByPhone(String phone);

    Boolean userExists(UserModel user);

    List<Object[]> checkUserUsingApp(int id, List<Object> listUser);

    void updateLastActive(int id);
}
