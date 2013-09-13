package uk.ac.hw.services.collabquiz.dao.impl;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class HibernateRepository {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    public HibernateRepository() {
        log.debug(getClass().getSimpleName() + " ctor()");
    }

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
       // return sessionFactory;
        return HibernateUtil.getSessionFactory();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
