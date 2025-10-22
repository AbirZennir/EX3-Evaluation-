package ma.projet.service;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.dao.IDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HommeService implements IDao<Homme> {

    @PersistenceContext
    private EntityManager em;

    @Override public Homme create(Homme o) { em.persist(o); return o; }
    @Override public Homme update(Homme o) { return em.merge(o); }
    @Override public void delete(Long id) { Homme h = findById(id); if (h != null) em.remove(h); }
    @Override public Homme findById(Long id) { return em.find(Homme.class, id); }
    @Override public List<Homme> findAll() { return em.createQuery("from Homme", Homme.class).getResultList(); }

    /** Épouses d’un homme entre deux dates (via dateDebut) */
    public List<Femme> findEpousesBetweenDates(Long hommeId, LocalDate d1, LocalDate d2) {
        return em.createQuery("""
                SELECT DISTINCT m.femme
                FROM Mariage m
                WHERE m.homme.id = :hid
                  AND m.dateDebut BETWEEN :d1 AND :d2
                """, Femme.class)
                .setParameter("hid", hommeId)
                .setParameter("d1", d1)
                .setParameter("d2", d2)
                .getResultList();
    }

    /** Tous les mariages d’un homme, en cours d’abord, puis par dateDebut ASC */
    public List<Mariage> findMariagesOf(Long hommeId) {
        return em.createQuery("""
                SELECT m
                FROM Mariage m
                WHERE m.homme.id = :hid
                ORDER BY CASE WHEN m.dateFin IS NULL THEN 0 ELSE 1 END,
                         m.dateDebut ASC
                """, Mariage.class)
                .setParameter("hid", hommeId)
                .getResultList();
    }

    /** Criteria API : nombre d’hommes mariés à ≥4 femmes entre deux dates (sur dateDebut) */
    public long countHommesMariesAQuatreFemmesBetween(LocalDate d1, LocalDate d2) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Mariage> m = cq.from(Mariage.class);
        Join<Mariage, Homme> h = m.join("homme");

        Predicate between = cb.between(m.get("dateDebut"), d1, d2);

        cq.select(h.get("id"))
                .where(between)
                .groupBy(h.get("id"))
                .having(cb.greaterThanOrEqualTo(cb.count(m), 4L));

        List<Long> ids = em.createQuery(cq).getResultList();
        return ids.size();
    }

    /** Impression formatée (exigence de l’énoncé) */
    public void printMariagesDetailsOf(Long hommeId) {
        Homme h = em.find(Homme.class, hommeId);
        if (h == null) {
            System.out.println("Homme introuvable: " + hommeId);
            return;
        }
        List<Mariage> all = findMariagesOf(hommeId);

        System.out.println("Nom : " + h.getNom() + " " + h.getPrenom());

        System.out.println("Mariages En Cours :");
        int i = 1;
        for (Mariage m : all.stream()
                .filter(x -> x.getDateFin() == null)
                .collect(Collectors.toList())) {
            System.out.printf("%d. Femme : %s %s   Date Début : %s    Nbr Enfants : %d%n",
                    i++, m.getFemme().getNom(), m.getFemme().getPrenom(),
                    m.getDateDebut(), m.getNbrEnfant());
        }

        System.out.println("\nMariages échoués :");
        i = 1;
        for (Mariage m : all.stream()
                .filter(x -> x.getDateFin() != null)
                .collect(Collectors.toList())) {
            System.out.printf("%d. Femme : %s %s   Date Début : %s    Date Fin : %s    Nbr Enfants : %d%n",
                    i++, m.getFemme().getNom(), m.getFemme().getPrenom(),
                    m.getDateDebut(), m.getDateFin(), m.getNbrEnfant());
        }
    }
}
