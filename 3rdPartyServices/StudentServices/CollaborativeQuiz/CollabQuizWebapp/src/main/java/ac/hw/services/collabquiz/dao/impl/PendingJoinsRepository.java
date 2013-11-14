package ac.hw.services.collabquiz.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ac.hw.services.collabquiz.dao.IPendingJoinsRepository;
import ac.hw.services.collabquiz.entities.PendingJoins;

public class PendingJoinsRepository extends HibernateRepository implements IPendingJoinsRepository {
    @Override
    public List<PendingJoins> list() {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        List<PendingJoins> pendingJoins = null;
        try {
            transaction = session.beginTransaction();
            pendingJoins = session.createQuery("from PendingJoins").list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return pendingJoins;
    }

    @Override
    public List<PendingJoins> getByID(String userJid) {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        List<PendingJoins> pendingJoins = new ArrayList<PendingJoins>();
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from PendingJoins where to_user = :user");
            query.setParameter("user", userJid);
            pendingJoins = query.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return pendingJoins;
    }
    
    @Override
    public List<PendingJoins> getPlayersByGroup(String groupName) {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        List<PendingJoins> joins = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from PendingJoins where groupName = :group");
            query.setParameter("group", groupName);
            joins = query.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return joins;
    }

    @Override
    public void insert(PendingJoins pendingJoins) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            //City city = new City();
            //city.setName(cityName);
            session.save(pendingJoins);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    @Override
    public void deleteGroup(String groupName)
    {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from pendingJoins where groupName = :group");
            query.setParameter("group", groupName);
            query.executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void physicalDelete(PendingJoins pendingJoins) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(pendingJoins);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
