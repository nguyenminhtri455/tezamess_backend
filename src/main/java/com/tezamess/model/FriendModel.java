package com.tezamess.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "friend")
public class FriendModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "useridrequest", updatable = false)
    private UserModel userRequest;

    @JoinColumn(name = "useridfriend", updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserModel userFriend;

    //status = 0 (da gui yeu cau ket ban)
    //status = 1 (da dong y yeu cau ket ban nhung nguoi yeu cau chua nhan duoc)
    //status = -1 (khong dong y yeu cau ket ban nhung nguoi yeu cau chua nhan duoc)
    //status = 2 (da la ban be)
    @Column(name = "status")
    private Integer status;

    public FriendModel() {
    }

    public FriendModel(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserModel getUserRequest() {
        return userRequest;
    }

    public void setUserRequest(UserModel userRequest) {
        this.userRequest = userRequest;
    }

    public UserModel getUserFriend() {
        return userFriend;
    }

    public void setUserFriend(UserModel userFriend) {
        this.userFriend = userFriend;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "com.tezamess.model.Friend[ id=" + id + " ]";
    }

}
