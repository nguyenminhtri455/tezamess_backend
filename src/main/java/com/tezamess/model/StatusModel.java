//package com.tezamess.model;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.List;
//import javax.persistence.Basic;
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToMany;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//
//@Entity
//@Table(name = "status")
//public class StatusModel implements Serializable {
//
//    @Basic(optional = false)
//    @NotNull
//    @Column(name = "createdate")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdate;
//
//    @Basic(optional = false)
//    @NotNull()
//    @Size(min = 1, max = 255)
//    @Column(name = "body")
//    private String body;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusid", fetch = FetchType.LAZY)
//    private List<NotificationModel> notificationList;
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusid", fetch = FetchType.LAZY)
//    private List<CommentModel> commentList;
//
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusid", fetch = FetchType.LAZY)
//    private List<LinkmediastatusModel> linkmediastatusList;
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Integer id;
//
//    @ManyToMany(mappedBy = "statusList", fetch = FetchType.LAZY)
//    private List<UserModel> userModelList;
//
//    @JoinColumn(name = "userid", referencedColumnName = "id")
//    @ManyToOne(fetch = FetchType.LAZY)
//    private UserModel userModel;
//
//    public StatusModel() {
//    }
//
//    public StatusModel(Integer id) {
//        this.id = id;
//    }
//
//    public StatusModel(Integer id, Date createdate, String body) {
//        this.id = id;
//        this.createdate = createdate;
//        this.body = body;
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public List<UserModel> getUserModelList() {
//        return userModelList;
//    }
//
//    public void setUserModelList(List<UserModel> userModelList) {
//        this.userModelList = userModelList;
//    }
//
//    public UserModel getUserModel() {
//        return userModel;
//    }
//
//    public void setUserModel(UserModel userModel) {
//        this.userModel = userModel;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof StatusModel)) {
//            return false;
//        }
//        StatusModel other = (StatusModel) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public String toString() {
//        return "com.tezamess.model.Status[ id=" + id + " ]";
//    }
//
//    public List<LinkmediastatusModel> getLinkmediastatusList() {
//        return linkmediastatusList;
//    }
//
//    public void setLinkmediastatusList(List<LinkmediastatusModel> linkmediastatusList) {
//        this.linkmediastatusList = linkmediastatusList;
//    }
//
//    public List<CommentModel> getCommentList() {
//        return commentList;
//    }
//
//    public void setCommentList(List<CommentModel> commentList) {
//        this.commentList = commentList;
//    }
//
//    public Date getCreatedate() {
//        return createdate;
//    }
//
//    public void setCreatedate(Date createdate) {
//        this.createdate = createdate;
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//
//    public List<NotificationModel> getNotificationList() {
//        return notificationList;
//    }
//
//    public void setNotificationList(List<NotificationModel> notificationList) {
//        this.notificationList = notificationList;
//    }
//
//}
