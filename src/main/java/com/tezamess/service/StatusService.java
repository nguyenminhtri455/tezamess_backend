package com.tezamess.service;

public interface StatusService {

    void postStatus(String json);

    void getStatuses(String json);

    void loadMoreStatused(String json);
}
