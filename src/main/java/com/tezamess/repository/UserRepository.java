/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tezamess.repository;

import com.tezamess.model.UserModel;
import java.util.List;

/**
 *
 * @author user
 */
public interface UserRepository {

    List<UserModel> findAll();

    UserModel login(UserModel user);

    UserModel register(UserModel user);

    UserModel updateUser(UserModel user);

    UserModel findUserById(int id);

    UserModel findUserByPhone(String phone);

    Boolean userExists(UserModel user);

    List<Object[]> checkUserUsingApp(List<Object> listUser);

}
