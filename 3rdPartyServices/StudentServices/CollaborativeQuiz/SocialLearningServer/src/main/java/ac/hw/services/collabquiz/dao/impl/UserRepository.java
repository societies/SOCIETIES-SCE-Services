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

import ac.hw.services.collabquiz.dao.IUserRepository;
import ac.hw.services.collabquiz.entities.User;



public class UserRepository extends HibernateRepository implements IUserRepository {
	
	private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

    @Override
    public List<User> list() {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        List<User> userScores = new ArrayList<User>();
        try {
            transaction = session.beginTransaction();
            userScores = session.createQuery("from User").list();
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
    public User getByJID(String jid) {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        User userScore = null;
        try {
            transaction = session.beginTransaction();
            userScore = (User) session.get(User.class, jid);
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
    public void insert(User user) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void update(User user) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void physicalDelete(User user) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}


