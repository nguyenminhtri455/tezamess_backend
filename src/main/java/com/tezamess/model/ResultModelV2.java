package com.tezamess.model;

import java.util.Date;

public class ResultModelV2 {

    private int status;
    private Object data;
    private String message;
    private Date time;

    public enum Status {
        SUCCESS(0), ERROR_JSON(1), ERROR_VALIDATE(2),
        ERROR_FAILED(3), ERROR_AUTHORICATION(4),
        ERROR_SERVER(5), ERROR_NOT_FOUND(6), ERROR_ACCESS_DENIED(7),
        CREATE_ROOM(8),ADD_FRIEND_REQUEST(9),ADD_FRIEND_RESPONSE(10),
        UNREAD_MESSAGE(11),FIND_FRIEND(12),LOADMORE_MESSAGE(13),
        FIND_ROOM(14);

        private int status;

        private Status(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }

    public ResultModelV2() {
    }

    public ResultModelV2(int status, Object data, String message, Date time) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return time;
    }

    public void setDate(Date time) {
        this.time = time;
    }

}
