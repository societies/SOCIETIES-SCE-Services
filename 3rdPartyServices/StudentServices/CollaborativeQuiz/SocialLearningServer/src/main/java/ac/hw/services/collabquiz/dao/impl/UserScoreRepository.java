package ac.hw.services.collabquiz.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ac.hw.services.collabquiz.dao.IUserScoreRepository;
import ac.hw.services.collabquiz.entities.UserScore;


public class UserScoreRepository extends HibernateRepository implements IUserScoreRepository {
	
	private static final Logger log = LoggerFactory.getLogger(UserScoreRepository.class);

    @Override
    public List<UserScore> list() {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        List<UserScore> userScores = new ArrayList<UserScore>();
        try {
            transaction = session.beginTransaction();
            userScores = session.createQuery("from UserScore").list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
            log.debug("ERROR¬!!!!");
            log.debug(e.toString());
        } finally {
            session.close();
        }
        return userScores;
    }

    @Override
    public UserScore getByJID(String jid) {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        UserScore userScore = null;
        try {
            transaction = session.beginTransaction();
            userScore = (UserScore) session.get(UserScore.class, jid);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return userScore;
    }

    @Override
    public void insert(UserScore userScore) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(userScore);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(UserScore userScore) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(userScore);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void physicalDelete(UserScore userScore) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(userScore);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}


