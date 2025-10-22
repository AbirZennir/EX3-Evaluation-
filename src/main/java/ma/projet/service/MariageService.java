package ma.projet.service;

import ma.projet.beans.Mariage;
import ma.projet.dao.IDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional
public class MariageService implements IDao<Mariage> {

    @PersistenceContext
    private EntityManager em;

    // ===== CRUD (signatures alignées avec IDao) =====
    @Override
    public Mariage create(Mariage o) {
        em.persist(o);
        return o;
    }

    @Override
    public Mariage update(Mariage o) {
        return em.merge(o);
    }

    @Override
    public void delete(Long id) {
        Mariage m = em.find(Mariage.class, id);
        if (m != null) em.remove(m);
    }

    @Override
    public Mariage findById(Long id) {
        return em.find(Mariage.class, id);
    }

    @Override
    public List<Mariage> findAll() {
        return em.createQuery("from Mariage", Mariage.class).getResultList();
    }
}
