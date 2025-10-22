package ma.projet.util;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

@Component
public class HibernateUtil {

    private SessionFactory sessionFactory;

    @PersistenceUnit
    public void init(EntityManagerFactory emf){
        this.sessionFactory = emf.unwrap(SessionFactoryImplementor.class);
    }

    public SessionFactory getSessionFactory(){
        return sessionFactory;
    }
}
