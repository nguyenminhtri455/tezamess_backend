/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tezamess.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author user
 */
@Entity
@Table(name = "member")
public class UserModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "phone", unique = true, nullable = false, length = 10)
    private String phone;

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

    @Column(name = "online")
    private boolean online;

    @Column(name = "lastactive")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastactive;


    public UserModel() {

    }

    public UserModel(Integer id) {
        this.id = id;
    }

    public UserModel(Integer id, String phone, String name, String password, Date birthday, boolean gender, String urlavatar, boolean online, Date lastactive) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.password = password;
        this.birthday = birthday;
        this.gender = gender;
        this.urlavatar = urlavatar;
        this.online = online;
        this.lastactive = lastactive;
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

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Date getLastactive() {
        return lastactive;
    }

    public void setLastactive(Date lastactive) {
        this.lastactive = lastactive;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserModel)) {
            return false;
        }
        UserModel other = (UserModel) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.appchat.model.UserModel[ id=" + id + " ]";
    }

}
