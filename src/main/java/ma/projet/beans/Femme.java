package ma.projet.beans;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("FEMME")
@NamedNativeQuery(
        name = "Femme.countChildrenBetweenDates",
        query = """
                SELECT COALESCE(SUM(m.nbr_enfant), 0) AS total
                FROM mariage m
                WHERE m.femme_id = :femmeId
                  AND m.date_debut >= :dateDebut
                  AND (m.date_fin IS NULL OR m.date_fin <= :dateFin)
                """,
        resultSetMapping = "Mapping.LongResult"
)
@NamedQuery(
        name = "Femme.marriedAtLeastTwice",
        query = """
                SELECT f
                FROM Femme f
                WHERE (SELECT COUNT(m) FROM Mariage m WHERE m.femme = f) >= 2
                """
)
@SqlResultSetMapping(
        name = "Mapping.LongResult",
        columns = @ColumnResult(name = "total", type = Long.class)
)
public class Femme extends Personne {
    // rien de plus pour l’instant
}
