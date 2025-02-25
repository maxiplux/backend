package app.quantun.backend.config.datasource.analytic;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "analyticEntityManagerFactory",
        transactionManagerRef = "analyticTransactionManager",
        basePackages = {"app.quantun.backend.source.repository.analytic"}
)
@Order(2)
class AnalyticDataSourceConfig {


    @Bean(name = "analyticDataSource")
    public DataSource analyticDataSource(@Qualifier("analyticHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "analyticEntityManagerFactory")
    public EntityManagerFactory analyticEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("analyticDataSource") DataSource dataSource) {
        val em = builder
                .dataSource(dataSource)
                .packages("app.quantun.backend.models.analytic.entity")
                .persistenceUnit("SourceAnalyticPool")
                .build();

        // Explicitly set the EntityManagerFactory interface to avoid conflict between
        // the EntityManagerFactory interfaces used by Spring and Hibernate.
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        // Explicitly set the EntityManagerFactory interface to avoid conflict between
        // the EntityManagerFactory interfaces used by Spring and Hibernate.
        em.setEntityManagerFactoryInterface(EntityManagerFactory.class);
        em.afterPropertiesSet();

        return em.getObject();
    }

    @Bean(name = "analyticTransactionManager")
    public JpaTransactionManager analyticTransactionManager(
            @Qualifier("analyticEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }


}
