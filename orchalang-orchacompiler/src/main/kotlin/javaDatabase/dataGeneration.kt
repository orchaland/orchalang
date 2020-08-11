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
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.io.File
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


class dataGeneration {
    fun generate() {
        val codeModel = JCodeModel()
        val className = "school." + "StudentDatabasePersistenceContext"
        val generatedClass = codeModel._class(JMod.PUBLIC, className, EClassType.CLASS)
        generatedClass.annotate(Configurable::class.java)
        val entityManagerFactory1: JFieldVar = generatedClass.field(JMod.NONE, EventHandler::class.java, "studentDatabase")
        entityManagerFactory1.annotate(Autowired::class.java)
        var method1 = generatedClass.method(JMod.NONE,  DataSource::class.java, "dataSource")
        method1.param(Environment::class.java,"env")
        method1.annotate(Bean::class.java).param("destroyMethod", "close" )
        var body1 =method1.body()
        val block = method1.body()
        val HikariConfigType = codeModel._ref(HikariConfig::class.java)
        val dataSourceConfigdecl= block.decl(HikariConfigType, "dataSourceConfig", JExpr._new(HikariConfigType))
        //val studentDatabaseinvok=JExpr.ref("studentDatabase")
        val getInputinvok=JExpr.invoke(entityManagerFactory1,"getInput")
        val getAdapterinvok=JExpr.invoke(getInputinvok,"getAdapter")
        val ConfigurablePropertiesType = codeModel._ref(ConfigurableProperties::class.java)
        val configdecl= block.decl(ConfigurablePropertiesType, "configurableProperties", getAdapterinvok)
        val condition = body1._if(configdecl._instanceof(codeModel.ref(DatabaseAdapter::class.java)))
        val block1 = condition._then()
        val DatabaseAdapter = codeModel._ref(DatabaseAdapter::class.java)
        val datadecl= block1.decl(DatabaseAdapter, "databaseAdapter", configdecl)
        val getConnectioninvok=JExpr.invoke(datadecl,"getConnection")
        val getDriverinvoke=JExpr.invoke(getConnectioninvok,"getDriver")
        val ligne_2_if=JExpr.invoke(dataSourceConfigdecl,"setDriverClassName").arg(getDriverinvoke)
        condition._then().add(ligne_2_if)
        val getUrlinvoke=JExpr.invoke(getConnectioninvok,"getUrl")
        val ligne_3_if=JExpr.invoke(dataSourceConfigdecl,"setJdbcUrl").arg(getUrlinvoke)
        condition._then().add(ligne_3_if)
        val getUsernameinvoke=JExpr.invoke(getConnectioninvok,"getUsername")
        val ligne_4_if=JExpr.invoke(dataSourceConfigdecl,"setUsername").arg(getUsernameinvoke)
        condition._then().add(ligne_4_if)
        val getPasswordinvoke=JExpr.invoke(getConnectioninvok,"getPassword")
        val ligne_5_if=JExpr.invoke(dataSourceConfigdecl,"setPassword").arg(getPasswordinvoke)
        condition._then().add(ligne_5_if)
        val newinvoke=JExpr._new(codeModel.ref(HikariDataSource::class.java)).arg(dataSourceConfigdecl)
        body1._return(newinvoke)
        method1 = generatedClass.method(JMod.NONE,  LocalContainerEntityManagerFactoryBean::class.java, "entityManagerFactory")
        method1.param(DataSource::class.java,"dataSource")
        method1.param(Environment::class.java,"env")
        method1.annotate(Bean::class.java)
        body1 =method1.body()
        val block2 = method1.body()
        val LocalContainerEntityManagerFactoryBeanType = codeModel._ref(LocalContainerEntityManagerFactoryBean::class.java)
        val LocalContainerEntityManagerFactoryBeandecl= block2.decl(LocalContainerEntityManagerFactoryBeanType, "entityManagerFactoryBean", JExpr._new(LocalContainerEntityManagerFactoryBeanType))
        val dataSourceref =JExpr.ref("dataSource")
        val setDataSourceinvok=JExpr.invoke( LocalContainerEntityManagerFactoryBeandecl,"setDataSource").arg(dataSourceref)
        body1.add(setDataSourceinvok)
        val HibernateJpaVendorAdapternew =JExpr._new(codeModel.ref(HibernateJpaVendorAdapter::class.java))
        val setJpaVendorAdapterinvoke=JExpr.invoke( LocalContainerEntityManagerFactoryBeandecl,"setJpaVendorAdapter").arg(HibernateJpaVendorAdapternew)
        body1.add(setJpaVendorAdapterinvoke)
        val getInputinvok1=JExpr.invoke(entityManagerFactory1,"getInput")
        val getAdapterinvok1=JExpr.invoke(getInputinvok1,"getAdapter")
        val ConfigurablePropertiesType1 = codeModel._ref(ConfigurableProperties::class.java)
        val configdecl1= block2.decl(ConfigurablePropertiesType1, "configurableProperties", getAdapterinvok1)
        val condition1 = body1._if(configdecl1._instanceof(codeModel.ref(orcha.lang.configuration.DatabaseAdapter::class.java)))
        val block3 = condition1._then()
        val DatabaseAdapter1 = codeModel._ref(orcha.lang.configuration.DatabaseAdapter::class.java)
        val datadecl1= block3.decl(DatabaseAdapter1, "databaseAdapter", configdecl1)
        val getConnectioninvok1=JExpr.invoke(datadecl1,"getConnection")
        val getEntityScanPackageinvoke1=JExpr.invoke(getConnectioninvok1,"getEntityScanPackage")
        val setPackagesToScaninvok=JExpr.invoke( LocalContainerEntityManagerFactoryBeandecl,"setPackagesToScan").arg(getEntityScanPackageinvoke1)
        condition1._then().add(setPackagesToScaninvok)
        val PropertiesType = codeModel._ref(Properties::class.java)
        val Propertiesdecl= block3.decl(PropertiesType, "jpaProperties", JExpr._new(PropertiesType))
        val getHibernateConfiginvoke1=JExpr.invoke(datadecl1,"getHibernateConfig")
        val getDialectinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getDialect")
        val putinvoke1=JExpr.invoke(Propertiesdecl,"put").arg("hibernate.dialect").arg(getDialectinvoke1)
        condition1._then().add(putinvoke1)
        val getHbm2ddlAutoinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getHbm2ddlAuto")
        val putinvoke2=JExpr.invoke(Propertiesdecl,"put").arg("hibernate.hbm2ddl.auto").arg(getHbm2ddlAutoinvoke1)
        condition1._then().add(putinvoke2)
        val getEjbNamingStrategyinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getEjbNamingStrategy")
        val putinvoke3=JExpr.invoke(Propertiesdecl,"put").arg("hibernate.ejb.naming_strategy").arg(getEjbNamingStrategyinvoke1)
        condition1._then().add(putinvoke3)
        val getShowSqlinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getShowSql")
        val putinvoke4=JExpr.invoke(Propertiesdecl,"put").arg("hibernate.show_sql").arg(getShowSqlinvoke1)
        condition1._then().add(putinvoke4)
        val getFormatSqlinvoke1=JExpr.invoke(getHibernateConfiginvoke1,"getFormatSql")
        val putinvoke5=JExpr.invoke(Propertiesdecl,"put").arg("hibernate.format_sql").arg(getFormatSqlinvoke1)
        condition1._then().add(putinvoke5)
        val setJpaPropertiesinvoke=JExpr.invoke(LocalContainerEntityManagerFactoryBeandecl,"setJpaProperties").arg(Propertiesdecl)
        condition1._then().add( setJpaPropertiesinvoke)
        method1 = generatedClass.method(JMod.NONE, JpaTransactionManager::class.java, "transactionManager")
        method1.param(EntityManagerFactory::class.java,"entityManagerFactory")
        method1.annotate(Bean::class.java)
        body1 =method1.body()
        val block4 = method1.body()
        val JpaTransactionManagerType = codeModel._ref(JpaTransactionManager::class.java)
        val JpaTransactionManagerdecl= block4.decl(JpaTransactionManagerType, "transactionManager", JExpr._new(JpaTransactionManagerType))
        val entityManagerFactoryinvok=JExpr.ref("entityManagerFactory")
        val setEntityManagerFactoryinvok=JExpr.invoke(JpaTransactionManagerdecl,"setEntityManagerFactory").arg(entityManagerFactoryinvok)
        body1.add(setEntityManagerFactoryinvok)
        body1._return(JpaTransactionManagerdecl)



        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source")
        codeModel.build(file)


    }
}
