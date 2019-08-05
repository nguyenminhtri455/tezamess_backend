package com.tezamess.repositoryimpl;

import com.tezamess.model.MediaModel;
import com.tezamess.model.StatusModel;
import com.tezamess.model.UserModel;
import com.tezamess.repository.StatusRepository;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(rollbackFor = Exception.class)
public class StatusRepositoryImpl implements StatusRepository {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public StatusModel postStatus(StatusModel statusModel) {
        Session session = sessionFactory.getCurrentSession();
        session.save(statusModel);

        statusModel.getListMediaModel().stream().map((s) -> {
            MediaModel mediaModel = new MediaModel();
            mediaModel.setStatus(statusModel);
            mediaModel.setUrl(s);
            return mediaModel;
        }).forEachOrdered((mediaModel) -> {
            session.save(mediaModel);
        });
        UserModel get = session.get(UserModel.class, statusModel.getUserPostStatus().getId());
        statusModel.setUserPostStatus(get);
        return statusModel;
    }

    @Override
    public List<StatusModel> getStatused() {
        Session session = sessionFactory.getCurrentSession();
        Query<StatusModel> createQuery = session.createQuery("FROM StatusModel s ORDER BY s.id DESC", StatusModel.class);
        createQuery.setFirstResult(0);
        createQuery.setMaxResults(10);
        
        List<StatusModel> resultList = createQuery.getResultList();
        resultList.forEach((s) -> {
            Query<MediaModel> createQuery1 = session.createQuery("FROM MediaModel m WHERE m.status.id = :statusId", MediaModel.class);
            createQuery1.setParameter("statusId", s.getId());
            List<MediaModel > resultList1 = createQuery1.getResultList();
            resultList1.forEach((m) -> {
                s.getListMediaModel().add(m.getUrl());
            });
        });
        return resultList;
    }

    @Override
    public List<StatusModel> loadMoreStatused(int start, int count) {
        Session session = sessionFactory.getCurrentSession();
        Query<StatusModel> createQuery = session.createQuery("FROM StatusModel s ORDER BY s.id DESC", StatusModel.class);
        createQuery.setFirstResult(start);
        createQuery.setMaxResults(count);
        
        List<StatusModel> resultList = createQuery.getResultList();
        resultList.forEach((s) -> {
            Query<MediaModel> createQuery1 = session.createQuery("FROM MediaModel m WHERE m.status.id = :statusId", MediaModel.class);
            createQuery1.setParameter("statusId", s.getId());
            List<MediaModel > resultList1 = createQuery1.getResultList();
            resultList1.forEach((m) -> {
                s.getListMediaModel().add(m.getUrl());
            });
        });
        return resultList;
    }

}
