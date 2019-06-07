package com.tezamess.model;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "room")
public class RoomModel implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "groupid", fetch = FetchType.EAGER)
    private Set<MessageModel> messageList;

    @ManyToMany(mappedBy = "roomModelList",fetch = FetchType.EAGER)
    private Set<UserModel> userModelList;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator", referencedColumnName = "id")
    private UserModel creator;

    public RoomModel() {
    }

    public RoomModel(Integer id) {
        this.id = id;
    }

    public RoomModel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserModel getCreator() {
        return creator;
    }

    public void setCreator(UserModel creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "com.tezamess.model.Room[ id=" + id + " ]";
    }

    public Set<UserModel> getUserModelList() {
        return userModelList;
    }

    public void setUserModelList(Set<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MessageModel> getMessageList() {
        return messageList;
    }

    public void setMessageList(Set<MessageModel> messageList) {
        this.messageList = messageList;
    }

}
