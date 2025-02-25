package app.quantun.backend.config.datasource.snowflake;

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
        entityManagerFactoryRef = "snowFlakeEntityManagerFactory",
        transactionManagerRef = "snowFlakeTransactionManager",
        basePackages = {"app.quantun.backend.source.repository.snowflake"}
)
@Order(3)
public class SnowFlakeDataSourceConfig {


    @Bean(name = "snowFlakeDataSource")
    public DataSource snowFlakeDataSource(@Qualifier("snowFlakeHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = "snowFlakeEntityManagerFactory")
    public EntityManagerFactory analyticEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("snowFlakeDataSource") DataSource dataSource) {
        val em = builder
                .dataSource(dataSource)
                .packages("app.quantun.backend.models.snowflake.entity")
                .persistenceUnit("SourceSnowFlakePool")
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

    @Bean(name = "snowFlakeTransactionManager")
    public JpaTransactionManager analyticTransactionManager(
            @Qualifier("snowFlakeEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
