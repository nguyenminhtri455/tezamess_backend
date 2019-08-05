package com.tezamess.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javax.persistence.Transient;
import javax.validation.constraints.Size;

@Entity
@Table(name = "status")
public class StatusModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", updatable = false)
    private UserModel userPostStatus;

    @Column(name = "createdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdate;

    @Size(min = 1, max = 255)
    @Column(name = "body")
    private String body;
    
    @Transient
    private List<String> listMediaModel;

    public StatusModel() {
        listMediaModel = new ArrayList();
    }

    public StatusModel(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserModel getUserPostStatus() {
        return userPostStatus;
    }

    public void setUserPostStatus(UserModel userPostStatus) {
        this.userPostStatus = userPostStatus;
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

    public List<String> getListMediaModel() {
        return listMediaModel;
    }

    public void setListMediaModel(List<String> listMediaModel) {
        this.listMediaModel = listMediaModel;
    }
       

    @Override
    public String toString() {
        return "StatusModel{" + "id=" + id + ", userPostStatus=" + userPostStatus + ", createdate=" + createdate + ", body=" + body + '}';
    }
}
