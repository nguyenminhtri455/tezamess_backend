package com.tezamess.model;

public class ResultModel {

    private String error;
    public ResultModel() {
    }

    public ResultModel(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
