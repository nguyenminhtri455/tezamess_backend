package com.tezamess.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "member")
public class UserModel implements Serializable {

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "participation",
            joinColumns = {
                @JoinColumn(name = "userid")},
            inverseJoinColumns = {
                @JoinColumn(name = "groupid")})
    private Set<RoomModel> roomModelList = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "phone", unique = true, nullable = false, length = 10)
    private String phone;

    @Column(name = "email", unique = true, nullable = true, length = 100)
    private String email;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "birthday")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "urlavatar", length = 500)
    private String urlavatar;

    @Column(name = "lastactive")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastactive;

    public UserModel() {
    }

    public UserModel(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getUrlavatar() {
        return urlavatar;
    }

    public void setUrlavatar(String urlavatar) {
        this.urlavatar = urlavatar;
    }

    public Date getLastactive() {
        return lastactive;
    }

    public void setLastactive(Date lastactive) {
        this.lastactive = lastactive;
    }

    @Override
    public String toString() {
        return "com.appchat.model.UserModel[ id=" + id + " ]";
    }

    public Set<RoomModel> getRoomModelList() {
        return roomModelList;
    }

    public void setRoomModelList(Set<RoomModel> roomModelList) {
        this.roomModelList = roomModelList;
    }

}
