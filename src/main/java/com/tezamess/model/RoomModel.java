package com.tezamess.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "room")
public class RoomModel implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room", fetch = FetchType.LAZY)
    private Set<MessageModel> messageList;

//    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinTable(name = "participation",
//            joinColumns = {
//                @JoinColumn(name = "groupid")},
//            inverseJoinColumns = {
//                @JoinColumn(name = "userid")})
//    private Set<UserModel> userModelList = new HashSet<>();
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ParticipationModel> participationModels = new HashSet();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator", referencedColumnName = "id", updatable = true)
    private UserModel creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type", referencedColumnName = "id", updatable = false)
    private TypeRoomModel typeRoomModel;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "com.tezamess.model.Room[ id=" + id + " ]";
    }

//    public Set<UserModel> getUserModelList() {
//        return userModelList;
//    }
//
//    public void setUserModelList(Set<UserModel> userModelList) {
//        this.userModelList = userModelList;
//    }
    public Set<ParticipationModel> getParticipationModels() {
        return participationModels;
    }

    public void setParticipationModels(Set<ParticipationModel> participationModels) {
        this.participationModels = participationModels;
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

    public TypeRoomModel getTypeRoomModel() {
        return typeRoomModel;
    }

    public void setTypeRoomModel(TypeRoomModel typeRoomModel) {
        this.typeRoomModel = typeRoomModel;
    }
}
