package school;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import orcha.lang.compiler.referenceimpl.springIntegration.Properties;
import orcha.lang.configuration.ConfigurableProperties;
import orcha.lang.configuration.DatabaseAdapter;
import orcha.lang.configuration.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configurable
public class StudentDatabasePersistenceContext {
    @Autowired
    EventHandler studentDatabase;
    HikariConfig dataSourceConfig;
    ConfigurableProperties configurableProperties;
    DatabaseAdapter databaseAdapter;
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;
    Properties jpaProperties;
    JpaTransactionManager transactionManager;

    @Bean(destroyMethod = "close")
    DataSource dataSource(Environment env) {
        dataSourceConfig = new HikariConfig();
        configurableProperties = studentDatabase.getInput().getAdapter();
        if (configurableProperties instanceof DatabaseAdapter) {
            databaseAdapter = configurableProperties;
            configurableProperties.setDriverClassName(databaseAdapter.getConnection().getDriver());
            configurableProperties.setJdbcUrl(databaseAdapter.getConnection().getUrl());
            configurableProperties.setUsername(databaseAdapter.getConnection().getUsername());
            configurableProperties.setPassword(databaseAdapter.getConnection().getPassword());
        }
        return new HikariDataSource(dataSourceConfig);
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Environment env) {
        entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
         entityManagerFactoryBean.setDataSource(dataSource);
         entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        configurableProperties = studentDatabase.getInput().getAdapter();
        if (configurableProperties instanceof DatabaseAdapter) {
            databaseAdapter = configurableProperties;
             entityManagerFactoryBean.setPackagesToScan(databaseAdapter.getConnection().getEntityScanPackage());
            jpaProperties = new Properties();
            jpaProperties.put("hibernate.dialect", databaseAdapter.getHibernateConfig().getDialect());
            jpaProperties.put("hibernate.hbm2ddl.auto", databaseAdapter.getHibernateConfig().getHbm2ddlAuto());
            jpaProperties.put("hibernate.ejb.naming_strategy", databaseAdapter.getHibernateConfig().getEjbNamingStrategy());
            jpaProperties.put("hibernate.show_sql", databaseAdapter.getHibernateConfig().getShowSql());
            jpaProperties.put("hibernate.format_sql", databaseAdapter.getHibernateConfig().getFormatSql());
             entityManagerFactoryBean.setJpaProperties(jpaProperties);
        }
        return  entityManagerFactoryBean;
    }

    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
