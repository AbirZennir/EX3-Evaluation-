package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.dao.IDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class FemmeService implements IDao<Femme> {

    @PersistenceContext
    private EntityManager em;

    // ===== CRUD (signatures alignées avec IDao) =====
    @Override
    public Femme create(Femme o) { em.persist(o); return o; }

    @Override
    public Femme update(Femme o) { return em.merge(o); }

    @Override
    public void delete(Long id) {
        Femme f = em.find(Femme.class, id);
        if (f != null) em.remove(f);
    }

    @Override
    public Femme findById(Long id) { return em.find(Femme.class, id); }

    @Override
    public List<Femme> findAll() {
        return em.createQuery("from Femme", Femme.class).getResultList();
    }

    // ===== Requêtes demandées =====

    /** Requête native nommée : nombre d’enfants d’une femme entre deux dates */
    public long countChildrenBetweenDates(Long femmeId, LocalDate d1, LocalDate d2) {
        Object val = em.createNamedQuery("Femme.countChildrenBetweenDates")
                .setParameter("femmeId", femmeId)
                .setParameter("dateDebut", d1)
                .setParameter("dateFin", d2)
                .getSingleResult();
        return (val == null) ? 0L : ((Number) val).longValue();
    }

    /** Requête nommée JPQL : femmes mariées au moins deux fois */
    public List<Femme> findMarriedAtLeastTwice() {
        return em.createNamedQuery("Femme.marriedAtLeastTwice", Femme.class)
                .getResultList();
    }
}
