package ac.hw.services.collabquiz.dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ac.hw.services.collabquiz.comms.CommsServerListener;
import ac.hw.services.collabquiz.dao.IUserAnsweredQRepository;
import ac.hw.services.collabquiz.entities.UserAnsweredQ;


public class UserAnsweredQRepository extends HibernateRepository implements IUserAnsweredQRepository {

	private static final Logger log = LoggerFactory.getLogger(UserAnsweredQRepository.class);

	@Override
	public List<UserAnsweredQ> list() {
		Session session = getSessionFactory().openSession();
		Transaction transaction = null;
		List<UserAnsweredQ> userAnsweredQ = null;
		try {
			transaction = session.beginTransaction();
			userAnsweredQ = session.createQuery("from UserAnsweredQ").list();
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return userAnsweredQ;
	}

	@Override
	public List<UserAnsweredQ> getByJID(String jid) {
		Session session =getSessionFactory().openSession();
		Transaction transaction = null;
		List<UserAnsweredQ> userAnsweredQ = null;
		try {
			transaction = session.beginTransaction();
			String hql = "from UserAnsweredQ where userJid = :userJid";
			userAnsweredQ = session.createQuery(hql)
					.setParameter("userJid", jid)
					.list();
			//userAnsweredQ = session.createQuery("from UserAnsweredQ WHERE userJid="+jid).list();
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return userAnsweredQ;
	}

	@Override
	public void insert(UserAnsweredQ userAnsweredQ) {
		Session session = getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.save(userAnsweredQ);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	} 
	@Override
	public void insertList(List<UserAnsweredQ> userAnsweredQ) {
		for(UserAnsweredQ ans : userAnsweredQ)
		{
			Session session = getSessionFactory().openSession();
			Transaction transaction = null;
			try {
				transaction = session.beginTransaction();
				session.save(ans);
				transaction.commit();
			} catch (HibernateException e) {
				transaction.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
	}

	@Override
	public void update(List<UserAnsweredQ> userAnsweredQ) {
		Session session = getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			for(UserAnsweredQ u : userAnsweredQ)
			{
				transaction = session.beginTransaction();
				session.save(u);
				transaction.commit();
			}
		} catch (HibernateException e) {
			log.debug("update failed!");
			log.debug(e.toString());
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void physicalDelete(UserAnsweredQ userAnsweredQ) {
		Session session = getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.delete(userAnsweredQ);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
}


