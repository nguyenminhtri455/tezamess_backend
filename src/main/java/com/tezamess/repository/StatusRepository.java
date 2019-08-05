package com.tezamess.repository;

import com.tezamess.model.StatusModel;
import java.util.List;

public interface StatusRepository {

    StatusModel postStatus(StatusModel statusModel);

    List<StatusModel> getStatused();

    List<StatusModel> loadMoreStatused(int start, int count);
}
