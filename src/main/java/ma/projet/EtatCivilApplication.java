package ma.projet;

import ma.projet.beans.Femme;
import ma.projet.beans.Homme;
import ma.projet.beans.Mariage;
import ma.projet.config.AppConfig;
import ma.projet.service.FemmeService;
import ma.projet.service.HommeService;
import ma.projet.service.MariageService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

public class EtatCivilApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        try {
            // 1) Enregistrer la config JPA/DataSource
            ctx.register(AppConfig.class);

            // 2) Scanner explicitement le package des services (au cas où)
            ctx.scan("ma.projet.service");

            // 3) Démarrer le contexte
            ctx.refresh();

            // === DEBUG : lister les beans de type *Service
            System.out.println("--- Beans *Service détectés ---");
            for (String b : ctx.getBeanDefinitionNames()) {
                if (b.toLowerCase().contains("service")) System.out.println(b);
            }

            // 4) Récupération robuste des services
            HommeService hommeService = getOrCreate(ctx, HommeService.class, "hommeService");
            FemmeService  femmeService = getOrCreate(ctx, FemmeService.class, "femmeService");
            MariageService mariageService = getOrCreate(ctx, MariageService.class, "mariageService");

            // ===== Données =====
            Homme h1 = createHomme("SAFI", "SAID", LocalDate.of(1965, 9, 1));
            Homme h2 = createHomme("AMRANI", "NABIL", LocalDate.of(1970, 3, 4));
            Homme h3 = createHomme("ZIANI", "OMAR", LocalDate.of(1968, 5, 10));
            Homme h4 = createHomme("TAZI", "HICHAM", LocalDate.of(1975, 7, 20));
            Homme h5 = createHomme("EL FASSI", "ADIL", LocalDate.of(1980, 1, 15));
            Arrays.asList(h1, h2, h3, h4, h5).forEach(h -> hommeService.create(h));

            Femme f1 = createFemme("SALIMA", "RAMI", LocalDate.of(1970, 1, 1));
            Femme f2 = createFemme("AMAL", "ALI", LocalDate.of(1972, 2, 2));
            Femme f3 = createFemme("WAFA", "ALAOUI", LocalDate.of(1975, 3, 3));
            Femme f4 = createFemme("KARIMA", "ALAMI", LocalDate.of(1969, 4, 4));
            Femme f5 = createFemme("HIBA", "SAIDI", LocalDate.of(1978, 5, 5));
            Femme f6 = createFemme("SARA", "ZOUITEN", LocalDate.of(1982, 6, 6));
            Femme f7 = createFemme("NOURA", "SALMI", LocalDate.of(1984, 7, 7));
            Femme f8 = createFemme("ILHAM", "BZ", LocalDate.of(1985, 8, 8));
            Femme f9 = createFemme("HANA", "TAZI", LocalDate.of(1990, 9, 9));
            Femme f10 = createFemme("RANIA", "MR", LocalDate.of(1992, 10, 10));
            Arrays.asList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10).forEach(f -> femmeService.create(f));

            createMariage(mariageService, h1, f1, LocalDate.of(1990, 9, 3), null, 4);
            createMariage(mariageService, h1, f2, LocalDate.of(1995, 9, 3), null, 2);
            createMariage(mariageService, h1, f3, LocalDate.of(2000, 11, 4), null, 3);
            createMariage(mariageService, h1, f4, LocalDate.of(1989, 9, 3), LocalDate.of(1990, 9, 3), 0);

            createMariage(mariageService, h2, f5, LocalDate.of(2001, 1, 1), null, 1);
            createMariage(mariageService, h2, f6, LocalDate.of(2002, 1, 1), null, 2);
            createMariage(mariageService, h2, f7, LocalDate.of(2003, 1, 1), null, 0);
            createMariage(mariageService, h2, f8, LocalDate.of(2004, 1, 1), null, 1);

            // ===== Affichages =====
            System.out.println("\n--- Liste des femmes ---");
            femmeService.findAll().forEach(f ->
                    System.out.println(f.getNom() + " " + f.getPrenom() + " - née le " + f.getDateNaissance()));

            var plusAgee = femmeService.findAll().stream()
                    .min((a, b) -> a.getDateNaissance().compareTo(b.getDateNaissance()))
                    .orElse(null);
            if (plusAgee != null) {
                System.out.println("\nFemme la plus âgée : " + plusAgee.getNom() + " " + plusAgee.getPrenom());
            }

            System.out.println("\nÉpouses de SAID SAFI entre 1990 et 2000 :");
            hommeService.findEpousesBetweenDates(h1.getId(),
                            LocalDate.of(1990, 1, 1), LocalDate.of(2000, 12, 31))
                    .forEach(f -> System.out.println("- " + f.getNom() + " " + f.getPrenom()));

            long enfants = femmeService.countChildrenBetweenDates(f1.getId(),
                    LocalDate.of(1989, 1, 1), LocalDate.of(2020, 12, 31));
            System.out.println("\nNombre d’enfants de " + f1.getNom() + " " + f1.getPrenom() + " : " + enfants);

            System.out.println("\nFemmes mariées au moins 2 fois :");
            femmeService.findMarriedAtLeastTwice()
                    .forEach(f -> System.out.println("- " + f.getNom() + " " + f.getPrenom()));

            long nb = hommeService.countHommesMariesAQuatreFemmesBetween(
                    LocalDate.of(2000, 1, 1), LocalDate.of(2005, 12, 31));
            System.out.println("\nHommes mariés à 4 femmes entre 2000 et 2005 : " + nb);

            System.out.println();
            hommeService.printMariagesDetailsOf(h1.getId());
        } finally {
            ctx.close();
        }
    }

    // ===== helpers =====
    private static <T> T getOrCreate(AnnotationConfigApplicationContext ctx, Class<T> type, String beanName) {
        try {
            // par type
            return ctx.getBean(type);
        } catch (NoSuchBeanDefinitionException e1) {
            try {
                // par nom explicite (lié au @Bean d'AppConfig)
                return ctx.getBean(beanName, type);
            } catch (NoSuchBeanDefinitionException e2) {
                // dernier recours : créer le bean et l’injecter (PersistenceContext inclus)
                System.out.println("⚠️ Bean manquant (" + type.getSimpleName() + "), création via factory…");
                return ctx.getAutowireCapableBeanFactory().createBean(type);
            }
        }
    }

    private static Homme createHomme(String nom, String prenom, LocalDate dn) {
        Homme h = new Homme();
        h.setNom(nom); h.setPrenom(prenom); h.setDateNaissance(dn);
        h.setAdresse("—"); h.setTelephone("—");
        return h;
    }
    private static ma.projet.beans.Femme createFemme(String nom, String prenom, LocalDate dn) {
        Femme f = new Femme();
        f.setNom(nom); f.setPrenom(prenom); f.setDateNaissance(dn);
        f.setAdresse("—"); f.setTelephone("—");
        return f;
    }
    private static void createMariage(MariageService svc, Homme h, Femme f,
                                      LocalDate dd, LocalDate df, int enfants) {
        Mariage m = new Mariage();
        m.setHomme(h); m.setFemme(f);
        m.setDateDebut(dd); m.setDateFin(df);
        m.setNbrEnfant(enfants);
        svc.create(m);
    }
}
