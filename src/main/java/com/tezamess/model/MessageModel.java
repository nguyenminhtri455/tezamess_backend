package com.tezamess.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "message")
public class MessageModel implements Serializable {

    @Column(name = "createdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdate;
 
    @Size(min = 1, max = 500)
    @Column(name = "body")
    private String body;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @JoinColumn(name = "groupid")
    @ManyToOne(fetch = FetchType.LAZY)
    private RoomModel room;

    @JoinColumn(name = "userid", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserModel user;

    public MessageModel() {
    }

    public MessageModel(Integer id) {
        this.id = id;
    }

    public MessageModel(Integer id, Date createdate, String body) {
        this.id = id;
        this.createdate = createdate;
        this.body = body;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public RoomModel getRoomid() {
        return room;
    }

    public void setRoomid(RoomModel room) {
        this.room = room;
    }

    public UserModel getUserid() {
        return user;
    }

    public void setUserid(UserModel user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "com.tezamess.model.Message[ id=" + id + " ]";
    }

    @Override
    public boolean equals(Object obj) {
         if (obj == null) {
            return false;
        }

        if (!(obj instanceof MessageModel)) {
            return false;
        }

        MessageModel messageModel = (MessageModel) obj;
        if (this.id == messageModel.getId()) {
            return true;
        }

        return false;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
}
