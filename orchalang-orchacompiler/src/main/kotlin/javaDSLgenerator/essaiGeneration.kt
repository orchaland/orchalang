package orcha.lang.compiler.referenceimpl.springIntegration.javaDSLgenerator

import com.helger.jcodemodel.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import orcha.lang.compiler.referenceimpl.springIntegration.Properties
import orcha.lang.configuration.ConfigurableProperties
import orcha.lang.configuration.DatabaseAdapter
import orcha.lang.configuration.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.ejb.config.JeeNamespaceHandler
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.io.File
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


class essaiGeneration {
    fun generate() {
        val codeModel = JCodeModel()
        val className = "school." + "StudentDatabasePersistenceContext"
        val generatedClass = codeModel._class(JMod.PUBLIC, className, EClassType.CLASS)

        /*    @Bean(destroyMethod = "close")
    DataSource dataSource(Environment env) {
        HikariConfig dataSourceConfig = new HikariConfig();
        ConfigurableProperties configurableProperties = studentDatabase.getInput().getAdapter();
        if(configurableProperties instanceof DatabaseAdapter){
            DatabaseAdapter databaseAdapter = (DatabaseAdapter)configurableProperties;
            dataSourceConfig.setDriverClassName(databaseAdapter.getConnection().getDriver());
            dataSourceConfig.setJdbcUrl(databaseAdapter.getConnection().getUrl());
            dataSourceConfig.setUsername(databaseAdapter.getConnection().getUsername());
            dataSourceConfig.setPassword(databaseAdapter.getConnection().getPassword());
        }
        return new HikariDataSource(dataSourceConfig);
    }*/
        generatedClass.annotate(Configurable::class.java)
        val entityManagerFactory: JFieldVar = generatedClass.field(JMod.NONE, EventHandler::class.java, "studentDatabase")
        entityManagerFactory.annotate(Autowired::class.java)
        var method = generatedClass.method(JMod.NONE,  DataSource::class.java, "dataSource")
        method.param(Environment::class.java,"env")
        method.annotate(Bean::class.java).param("destroyMethod", "close" )
        var body =method.body()
        val hikariConfigField: JFieldVar = generatedClass.field(JMod.NONE, HikariConfig::class.java, "dataSourceConfig")
        val newHikariConfig =JExpr._new(codeModel.ref(HikariConfig::class.java))
         val ligne1 = hikariConfigField.assign(newHikariConfig)
        body.add(ligne1)
        /*  val dataSourceConfig = codeModel.directClass("com.zaxxer.hikari.HikariConfig")
        val newHikariConfig =JExpr._new(codeModel.ref(HikariConfig::class.java))
        val block = method.body()
        val lhs: JVar = block.decl(dataSourceConfig , "dataSourceConfig",newHikariConfig)*/
        val configurablePropertiesfield : JFieldVar = generatedClass.field(JMod.NONE, ConfigurableProperties::class.java, "configurableProperties")
        val studentDatabaseinvok=JExpr.ref("studentDatabase")
        val getInputinvok=JExpr.invoke(studentDatabaseinvok,"getInput")
        val getAdapterinvok=JExpr.invoke(getInputinvok,"getAdapter")
        val ligne2=configurablePropertiesfield.assign(getAdapterinvok)
        body.add(ligne2)
      //  val databaseAdapterfield=JExpr.ref("DatabaseAdapter")
        val condition = body._if(configurablePropertiesfield._instanceof(codeModel.ref(DatabaseAdapter::class.java)))
        val databaseAdapterField: JFieldVar = generatedClass.field(JMod.NONE, DatabaseAdapter::class.java, "databaseAdapter")
        val ligne_1_if=databaseAdapterField.assign(configurablePropertiesfield)
        condition._then().add(ligne_1_if)
        val getConnectioninvok=JExpr.invoke(databaseAdapterField,"getConnection")
        val getDriverinvoke=JExpr.invoke(getConnectioninvok,"getDriver")
        val ligne_2_if=JExpr.invoke(configurablePropertiesfield,"setDriverClassName").arg(getDriverinvoke)
        condition._then().add(ligne_2_if)
        val getUrlinvoke=JExpr.invoke(getConnectioninvok,"getUrl")
        val ligne_3_if=JExpr.invoke(configurablePropertiesfield,"setJdbcUrl").arg(getUrlinvoke)
        condition._then().add(ligne_3_if)
        val getUsernameinvoke=JExpr.invoke(getConnectioninvok,"getUsername")
        val ligne_4_if=JExpr.invoke(configurablePropertiesfield,"setUsername").arg(getUsernameinvoke)
        condition._then().add(ligne_4_if)
        val getPasswordinvoke=JExpr.invoke(getConnectioninvok,"getPassword")
        val ligne_5_if=JExpr.invoke(configurablePropertiesfield,"setPassword").arg(getPasswordinvoke)
        condition._then().add(ligne_5_if)
        //return new HikariDataSource(dataSourceConfig);
        val newinvoke=JExpr._new(codeModel.ref(HikariDataSource::class.java)).arg(hikariConfigField)
        body._return(newinvoke)

/*  @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Environment env) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        ConfigurableProperties configurableProperties = studentDatabase.getInput().getAdapter();
        if(configurableProperties instanceof DatabaseAdapter) {
            DatabaseAdapter databaseAdapter = (DatabaseAdapter) configurableProperties;
            entityManagerFactoryBean.setPackagesToScan(databaseAdapter.getConnection().getEntityScanPackage());
            Properties jpaProperties = new Properties();
            jpaProperties.put("hibernate.dialect", databaseAdapter.getHibernateConfig().getDialect());
            jpaProperties.put("hibernate.hbm2ddl.auto", databaseAdapter.getHibernateConfig().getHbm2ddlAuto());
            jpaProperties.put("hibernate.ejb.naming_strategy", databaseAdapter.getHibernateConfig().getEjbNamingStrategy());
            jpaProperties.put("hibernate.show_sql", databaseAdapter.getHibernateConfig().getShowSql());
            jpaProperties.put("hibernate.format_sql", databaseAdapter.getHibernateConfig().getFormatSql());

            entityManagerFactoryBean.setJpaProperties(jpaProperties);
        }

        return entityManagerFactoryBean;
    }
*/

        method = generatedClass.method(JMod.NONE,  LocalContainerEntityManagerFactoryBean::class.java, "entityManagerFactory")
        method.param(DataSource::class.java,"dataSource")
        method.param(Environment::class.java,"env")
        method.annotate(Bean::class.java)
        body =method.body()
        val entityManagerFactoryBeanField: JFieldVar = generatedClass.field(JMod.NONE,   LocalContainerEntityManagerFactoryBean::class.java, "entityManagerFactoryBean")
        val newentityManagerFactoryBeanField =JExpr._new(codeModel.ref(LocalContainerEntityManagerFactoryBean::class.java))
        val ligne1_local = entityManagerFactoryBeanField.assign(newentityManagerFactoryBeanField)
        body.add(ligne1_local)
        val  entityManagerFactoryBeaninvok=JExpr.ref(" entityManagerFactoryBean")
        val dataSourceref =JExpr.ref("dataSource")
        val setDataSourceinvok=JExpr.invoke( entityManagerFactoryBeaninvok,"setDataSource").arg(dataSourceref)
        body.add(setDataSourceinvok)
        val HibernateJpaVendorAdapternew =JExpr._new(codeModel.ref(HibernateJpaVendorAdapter::class.java))
        val setJpaVendorAdapterinvoke=JExpr.invoke( entityManagerFactoryBeaninvok,"setJpaVendorAdapter").arg(HibernateJpaVendorAdapternew)
        body.add(setJpaVendorAdapterinvoke)
        body.add(ligne2)
        val condition1 = body._if(configurablePropertiesfield._instanceof(codeModel.ref(DatabaseAdapter::class.java)))
        val ligne_22_if=databaseAdapterField.assign(configurablePropertiesfield)
        condition1._then().add(ligne_22_if)
        val getEntityScanPackageinvoke=JExpr.invoke(getConnectioninvok,"getEntityScanPackage")
        val setPackagesToScaninvok=JExpr.invoke( entityManagerFactoryBeaninvok,"setPackagesToScan").arg(getEntityScanPackageinvoke)
        condition1._then().add(setPackagesToScaninvok)
        val jpaPropertiesField: JFieldVar = generatedClass.field(JMod.NONE, Properties::class.java, "jpaProperties")
        val newpropertiesConfig =JExpr._new(codeModel.ref(Properties::class.java))
        val ligne22= jpaPropertiesField.assign(newpropertiesConfig)
        condition1._then().add(ligne22)

        val databaseAdapterref=JExpr.ref("databaseAdapter")
        val getHibernateConfiginvoke1=JExpr.invoke(databaseAdapterref,"getHibernateConfig")
        val getDialectinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getDialect")
        val putinvoke1=JExpr.invoke(jpaPropertiesField,"put").arg("hibernate.dialect").arg(getDialectinvoke1)
        condition1._then().add(putinvoke1)
        val getHbm2ddlAutoinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getHbm2ddlAuto")
        val putinvoke2=JExpr.invoke(jpaPropertiesField,"put").arg("hibernate.hbm2ddl.auto").arg(getHbm2ddlAutoinvoke1)
        condition1._then().add(putinvoke2)
        val getEjbNamingStrategyinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getEjbNamingStrategy")
        val putinvoke3=JExpr.invoke(jpaPropertiesField,"put").arg("hibernate.ejb.naming_strategy").arg(getEjbNamingStrategyinvoke1)
        condition1._then().add(putinvoke3)
        val getShowSqlinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getShowSql")
        val putinvoke4=JExpr.invoke(jpaPropertiesField,"put").arg("hibernate.show_sql").arg(getShowSqlinvoke1)
        condition1._then().add(putinvoke4)
        val getFormatSqlinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getFormatSql")
        val putinvoke5=JExpr.invoke(jpaPropertiesField,"put").arg("hibernate.format_sql").arg(getFormatSqlinvoke1)
        condition1._then().add(putinvoke5)
        val setJpaPropertiesinvoke=JExpr.invoke(entityManagerFactoryBeaninvok,"setJpaProperties").arg(jpaPropertiesField)
        condition1._then().add( setJpaPropertiesinvoke)
        body._return(entityManagerFactoryBeaninvok)









































    /*@Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }*/
        method = generatedClass.method(JMod.NONE, JpaTransactionManager::class.java, "transactionManager")
        method.param(EntityManagerFactory::class.java,"entityManagerFactory")
        method.annotate(Bean::class.java)
        body =method.body()
        val transactionManagerField: JFieldVar = generatedClass.field(JMod.NONE,  JpaTransactionManager::class.java, "transactionManager")
        val newJpaTransactionManager =JExpr._new(codeModel.ref(JpaTransactionManager::class.java))
        val ligne1_tran = transactionManagerField.assign(newJpaTransactionManager)
        body.add(ligne1_tran)
        val entityManagerFactoryinvok=JExpr.ref("entityManagerFactory")
        val setEntityManagerFactoryinvok=JExpr.invoke(transactionManagerField,"setEntityManagerFactory").arg(entityManagerFactoryinvok)
        body.add(setEntityManagerFactoryinvok)
        body._return(transactionManagerField)




        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source")
        codeModel.build(file)


    }
}
