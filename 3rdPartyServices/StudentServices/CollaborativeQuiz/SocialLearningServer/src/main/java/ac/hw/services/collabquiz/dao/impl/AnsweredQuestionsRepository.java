package ac.hw.services.collabquiz.dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ac.hw.services.collabquiz.dao.IAnsweredQuestionsRepository;
import ac.hw.services.collabquiz.entities.AnsweredQuestions;


public class AnsweredQuestionsRepository extends HibernateRepository implements IAnsweredQuestionsRepository {

	private static final Logger log = LoggerFactory.getLogger(AnsweredQuestionsRepository.class);

	@Override
	public List<AnsweredQuestions> list() {
		Session session = getSessionFactory().openSession();
		Transaction transaction = null;
		List<AnsweredQuestions> userAnsweredQ = null;
		try {
			transaction = session.beginTransaction();
			userAnsweredQ = session.createQuery("from AnsweredQuestions").list();
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
	public List<AnsweredQuestions> getByJID(String jid) {
		Session session =getSessionFactory().openSession();
		Transaction transaction = null;
		List<AnsweredQuestions> userAnsweredQ = null;
		try {
			transaction = session.beginTransaction();
			String hql = "from AnsweredQuestions where userID = :userJid and cisName = :name";
			userAnsweredQ = session.createQuery(hql)
					.setParameter("userJid", jid)
					.setParameter("name", null)
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
	public List<AnsweredQuestions> getByCisName(String cisName) {
		Session session =getSessionFactory().openSession();
		Transaction transaction = null;
		List<AnsweredQuestions> userAnsweredQ = null;
		try {
			transaction = session.beginTransaction();
			String hql = "from AnsweredQuestions where cisName = :name";
			userAnsweredQ = session.createQuery(hql)
					.setParameter("name", cisName)
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
	public void insert(AnsweredQuestions userAnsweredQ) {
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
	public void insertList(List<AnsweredQuestions> userAnsweredQ) {
		for(AnsweredQuestions ans : userAnsweredQ)
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
	public void update(List<AnsweredQuestions> userAnsweredQ) {
		Session session = getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			for(AnsweredQuestions u : userAnsweredQ)
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
    public void deleteAll(String id)
    {
        Session session =getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from AnsweredQuestions where userJid = :id");
            query.setParameter("id", id);
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
	public void physicalDelete(AnsweredQuestions userAnsweredQ) {
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


