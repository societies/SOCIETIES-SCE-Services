package ac.hw.services.collabquiz.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ac.hw.services.collabquiz.dao.IUserGroupsRepository;
import ac.hw.services.collabquiz.entities.UserGroups;

public class UserGroupsRepository extends HibernateRepository implements IUserGroupsRepository {
    @Override
    public List<UserGroups> list() {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        List<UserGroups> userGroups = null;
        try {
            transaction = session.beginTransaction();
            userGroups = session.createQuery("from UserGroups").list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return userGroups;
    }

    @Override
    public UserGroups getByID(String userJid) {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        UserGroups userGroups = null;
        try {
            transaction = session.beginTransaction();
            userGroups = (UserGroups) session.get(UserGroups.class, userJid);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return userGroups;
    }
    
    @Override
    public List<String> listUsers(int groupId)
    {
    	List<UserGroups> usersGroup = getListByID(groupId);
    	List<String> users = new ArrayList<String>();
    	for(UserGroups u : usersGroup)
    	{
    		users.add(u.getUserJid().substring(0, u.getUserJid().indexOf('.')));
    	}
    	return users;
    }
    
    @Override
    public List<UserGroups> getListByID(int groupId) {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        List<UserGroups> userGroups = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from UserGroups where group_id = :group");
            query.setParameter("group", groupId);
            userGroups = query.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return userGroups;
    }

    @Override
    public void insert(UserGroups userGroup) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            //City city = new City();
            //city.setName(cityName);
            session.save(userGroup);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteAll(String groupID)
    {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from UserGroups where group_id = :group");
            query.setParameter("group", groupID);
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
    public void deleteUser(String userID)
    {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from UserGroups where userJid = :userJid");
            query.setParameter("userJid", userID);
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
    public void update(UserGroups userGroup) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(userGroup);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void physicalDelete(UserGroups userGroup) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(userGroup);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
