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
@Table(name = "tempmessage")
public class TempMessageModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "statusmessage")
    private int statusMessage;

    @JoinColumn(name = "idmember", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private UserModel idmember;

    @JoinColumn(name = "idmessage", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private MessageModel idmessage;

    public TempMessageModel() {
    }

    public TempMessageModel(Integer id) {
        this.id = id;
    }

    public TempMessageModel(Integer id, int statusMessage) {
        this.id = id;
        this.statusMessage = statusMessage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getStatusmessage() {
        return statusMessage;
    }

    public void setStatusmessage(int statusMessage) {
        this.statusMessage = statusMessage;
    }

    public UserModel getIdmember() {
        return idmember;
    }

    public void setIdmember(UserModel idmember) {
        this.idmember = idmember;
    }

    public MessageModel getIdmessage() {
        return idmessage;
    }

    public void setIdmessage(MessageModel idmessage) {
        this.idmessage = idmessage;
    }

    @Override
    public String toString() {
        return "com.tezamess.model.Tempmessage[ id=" + id + " ]";
    }

}
