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
    @JoinColumn(name = "useridrequest")
    private UserModel useridrequest;

    @JoinColumn(name = "useridfriend")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private UserModel useridfriend;

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

    public UserModel getUseridrequest() {
        return useridrequest;
    }

    public void setUseridrequest(UserModel useridrequest) {
        this.useridrequest = useridrequest;
    }

    public UserModel getUseridfriend() {
        return useridfriend;
    }

    public void setUseridfriend(UserModel useridfriend) {
        this.useridfriend = useridfriend;
    }

    @Override
    public String toString() {
        return "com.tezamess.model.Friend[ id=" + id + " ]";
    }

}
