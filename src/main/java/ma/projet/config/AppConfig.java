package ma.projet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan(basePackages = "ma.projet") // scanne tout le projet
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/etat_civil_db"
                + "?createDatabaseIfNotExist=true"
                + "&useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=UTC");
        ds.setUsername("root");   // adapte selon ta machine
        ds.setPassword("");       // adapte selon ta machine
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(ds);
        emf.setPackagesToScan("ma.projet.beans");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties p = new Properties();
        p.put("hibernate.hbm2ddl.auto", "update");
        // Tu peux aussi supprimer cette ligne (Hibernate détecte tout seul)
        p.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        p.put("hibernate.show_sql", "true");
        p.put("hibernate.format_sql", "true");
        emf.setJpaProperties(p);

        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
        JpaTransactionManager tx = new JpaTransactionManager();
        tx.setEntityManagerFactory(emf.getObject());
        return tx;
    }

    // ===== Filets de sécurité : déclarations explicites des services =====
    @Bean public ma.projet.service.HommeService hommeService() { return new ma.projet.service.HommeService(); }
    @Bean public ma.projet.service.FemmeService femmeService() { return new ma.projet.service.FemmeService(); }
    @Bean public ma.projet.service.MariageService mariageService() { return new ma.projet.service.MariageService(); }
}
