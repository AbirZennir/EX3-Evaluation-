package ma.projet.beans;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mariage")
public class Mariage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "homme_id")
    private Homme homme;

    @ManyToOne(optional = false) @JoinColumn(name = "femme_id")
    private Femme femme;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin; // null = en cours

    @Column(name = "nbr_enfant", nullable = false)
    private int nbrEnfant;

    // getters/setters
    public Long getId() { return id; }
    public Homme getHomme() { return homme; }
    public Femme getFemme() { return femme; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public int getNbrEnfant() { return nbrEnfant; }

    public void setId(Long id) { this.id = id; }
    public void setHomme(Homme homme) { this.homme = homme; }
    public void setFemme(Femme femme) { this.femme = femme; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public void setNbrEnfant(int nbrEnfant) { this.nbrEnfant = nbrEnfant; }
}
