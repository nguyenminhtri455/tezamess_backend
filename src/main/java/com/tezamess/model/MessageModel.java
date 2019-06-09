package com.tezamess.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "message")
public class MessageModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "createdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdate;

    @NotNull
    @Column(name = "body")
    private String body;

    @JoinColumn(name = "groupid")
    @ManyToOne(fetch = FetchType.EAGER)
    private RoomModel groupid;

    @JoinColumn(name = "userid")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserModel userid;

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

    public RoomModel getGroupid() {
        return groupid;
    }

    public void setGroupid(RoomModel groupid) {
        this.groupid = groupid;
    }

    public UserModel getUserid() {
        return userid;
    }

    public void setUserid(UserModel userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "com.tezamess.model.Message[ id=" + id + " ]";
    }

}
