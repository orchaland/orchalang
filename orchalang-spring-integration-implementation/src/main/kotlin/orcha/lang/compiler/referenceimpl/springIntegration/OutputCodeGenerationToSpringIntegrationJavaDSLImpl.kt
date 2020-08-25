package orcha.lang.compiler.referenceimpl.springIntegration

import com.helger.jcodemodel.*

import com.helger.jcodemodel.writer.FileCodeWriter
import com.helger.jcodemodel.writer.JCMWriter
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.syntax.ComputeInstruction
import orcha.lang.compiler.syntax.SendInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.*
import orcha.lang.configuration.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import java.io.File
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.nio.charset.StandardCharsets
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

class OutputCodeGenerationToSpringIntegrationJavaDSLImpl : OutputCodeGenerationToSpringIntegrationJavaDSL {

    private var applicationToMessageGenerated: Boolean = false
    var generatedClass : JDefinedClass ? = null
    val codeModel = JCodeModel()

    override fun orchaMetadata(orchaMetadata: OrchaMetadata) {

        var className = orchaMetadata.domainAsCapitalizedConcatainedString!!.decapitalize() + "." + orchaMetadata.titleAsCapitalizedConcatainedString + "Application"

        log.info("Generated class name: " + className)

        generatedClass = codeModel._class(JMod.PUBLIC, className , EClassType.CLASS)

        val jAnnotation: JAnnotationUse = generatedClass!!.annotate(SpringBootApplication::class.java)
        jAnnotation.paramArray("scanBasePackages", orchaMetadata.domain);

        log.info("Generation of the main program")

        val method= generatedClass!!.method(JMod.PUBLIC or JMod.STATIC, codeModel.VOID, "main")

        val springRef = codeModel.ref("String")

        method.param(JMod.NONE, springRef.array(), "args")

        val body = method.body()
       /* val springInvoke=JExpr._new(codeModel.ref(SpringApplicationBuilder::class.java))

        className = orchaMetadata.titleAsCapitalizedConcatainedString + "Application"

        val orchaInvoke=JExpr.ref(className)
        val classInvoke=JExpr.refthis(orchaInvoke,"class")
        springInvoke.arg(classInvoke)
        val webInvoke=JExpr.invoke(springInvoke,"web")
        val WebApplicationTypeInvoke=JExpr.ref("WebApplicationType")
        val NONEInvoke=JExpr.refthis(WebApplicationTypeInvoke,"NONE")
        webInvoke.arg(NONEInvoke)
        val runInvoke=JExpr.invoke(webInvoke,"run")
        val argsInvoke=JExpr.ref("args")
        runInvoke.arg(argsInvoke)
        body.add(runInvoke)*/
       /*  val method = generatedClass.method(JMod.PUBLIC or JMod.STATIC, codeModel.VOID, "main")

        val stringRef = codeModel.ref("String")
        method.param(JMod.NONE, stringRef.array(), "args")
        val  body = method.body()*/
        val springApplicationinvoke1=JExpr.ref("SpringApplication")
        val orchaInvoke = JExpr.ref("JPAApplication")
        val classInvoke = JExpr.ref(orchaInvoke, "class")
        val argsref1=JExpr.ref("args")
        val runinvok = JExpr.invoke(springApplicationinvoke1,"run").arg(classInvoke).arg(argsref1)
        val ConfigurableApplicationContextType = codeModel._ref(ConfigurableApplicationContext::class.java)
        val configdecl= body.decl(ConfigurableApplicationContextType, "context", runinvok)
       val PopulateDatabaseType = codeModel.ref("PopulateDatabase")
        val  contextInvoke = JExpr.ref(" context")
        val getBeaninvok = JExpr.invoke(contextInvoke,"getBean").arg("populateDatabase")
        val populateDatabasecl= body.decl(PopulateDatabaseType, "populateDatabase", getBeaninvok)

        val ListeType = codeModel._ref(List::class.java)
        val resultsfilde=body.decl(ListeType, "results")
        val bloc1= body._try()
              val bloc =bloc1.body()
        val studentinvoke1= JExpr._new(codeModel.ref("StudentDomain")).arg("Morgane").arg(21).arg(-1)
        val StudentDomainType = codeModel.ref("StudentDomain")
        val StudentDomainfilde=bloc.decl(StudentDomainType, "student",studentinvoke1)
        val saveinvoke=JExpr.invoke(populateDatabasecl,"saveStudent").arg(StudentDomainfilde)
        bloc.add(saveinvoke)
         //results = populateDatabase.readDatabase();
       val readDatabaseinvoke =JExpr.invoke(populateDatabasecl,"readDatabase")
       val assignaa= resultsfilde.assign(readDatabaseinvoke)
        bloc.add(assignaa)
        val systeminvoker=JExpr.ref("System")
        val outinvoker=JExpr.ref(systeminvoker,"out")
        val printinvoker=JExpr.invoke(outinvoker,"println").arg("database:").arg(resultsfilde)
        bloc.add(printinvoker)
        val catchTry : JCatchBlock=bloc1._catch(codeModel.ref(Exception::class.java))
        val exception =catchTry.param("e")
        val eeee=JExpr.ref("e")
        val printinvoker1=JExpr.invoke(outinvoker,"println").arg(">>>>>> Caught exception:+").arg(eeee)
        catchTry.body().add(printinvoker1)

    }

    override fun inputAdapter(eventHandler: EventHandler, nextIntegrationNodes: List<IntegrationNode>) {
        val adapter = eventHandler.input!!.adapter
        log.info("Generation of the output code for " + adapter)
        when(adapter){
            is InputFileAdapter -> {
                val inputFileAdapter: InputFileAdapter = adapter as InputFileAdapter
                var method: JMethod = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, eventHandler.name + "Flow")
                method.annotate(Bean::class.java)
                var body = method.body()
                val fromInvoke: JInvocation = codeModel.ref(IntegrationFlows::class.java).staticInvoke("from")
                val inboundAdapterInvoke: JInvocation = codeModel.ref(org.springframework.integration.file.dsl.Files::class.java).staticInvoke("inboundAdapter")
                inboundAdapterInvoke.arg(JExpr._new(codeModel.ref(File::class.java)).arg(inputFileAdapter.directory))
                val patternFilterInvoke = JExpr.invoke(inboundAdapterInvoke, "patternFilter").arg(inputFileAdapter.filenamePattern)
                fromInvoke.arg(patternFilterInvoke)
                val holder = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "a", null)
                val aLambda = JLambda()
                val arr = aLambda.addParam("a")
                val setBody: JBlock = aLambda.body()
                val fixedDelayInvoke: JInvocation = codeModel.ref(Pollers::class.java).staticInvoke("fixedDelay")
                fixedDelayInvoke.arg(1000)
                setBody.add(JExpr.invoke(codeModel, holder, "poller").arg(fixedDelayInvoke))
                fromInvoke.arg(aLambda)
                val enrichHeadersInvoke =JExpr.invoke(codeModel,fromInvoke,"enrichHeaders")
                val holder2 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "h", null)
                val aLambda2 = JLambda()
                val arr2= aLambda2.addParam("h")
                val setBody2: JBlock = aLambda2.body()
                setBody2.add(JExpr.invoke(codeModel,holder2,"headerExpression").arg("messageID").arg("headers['id'].toString()"))
                var channelInvoke: JInvocation? =null
                val nextIntegrationNode = nextIntegrationNodes[0]
                when(nextIntegrationNode.instruction){
                    is ComputeInstruction -> {
                        val compute: ComputeInstruction = nextIntegrationNode.instruction as ComputeInstruction
                        val application: Application = compute.configuration as Application
                        channelInvoke = JExpr.invoke(enrichHeadersInvoke.arg(aLambda2), "channel").arg(application.name + "Channel.input")
                    }
                }

                val getInvoke = JExpr.invoke(channelInvoke, "get")
                body._return(getInvoke)
            }
            is DatabaseAdapter -> {
                val entityManagerFactory: JFieldVar = generatedClass!!
                        .field(JMod.PRIVATE, EntityManagerFactory::class.java, "entityManagerFactory")
                entityManagerFactory.annotate(Autowired::class.java)
                val inputAdapter: DatabaseAdapter = adapter as DatabaseAdapter
                var method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, eventHandler.name +"Flow")
                method.annotate(Bean::class.java)
                var body =method.body()
                val fromInvoke = codeModel.ref(IntegrationFlows::class.java).staticInvoke("from")
                val inboundAdapterInvoke: JInvocation = codeModel.ref(org.springframework.integration.jpa.dsl.Jpa::class.java)
                        .staticInvoke("inboundAdapter")
                val thisInvoke = JExpr.ref( "this")
                val entityManagerFactoryInvoke = JExpr.ref( thisInvoke,"entityManagerFactory")
                inboundAdapterInvoke.arg(entityManagerFactoryInvoke)
                val entityClassinvoke=JExpr.invoke(inboundAdapterInvoke,"entityClass")
                val studentdomainref=JExpr.ref("StudentDomain")
                val classref=JExpr.ref(studentdomainref,"class")
                entityClassinvoke.arg(classref)
                val maxResultsinvoke=JExpr.invoke(entityClassinvoke,"maxResults")
                val valinref=JExpr.ref("1")
                maxResultsinvoke.arg(valinref)
                val expectSingleResultinvoke=JExpr.invoke(maxResultsinvoke,"expectSingleResult")
                val trueinref=JExpr.ref("true")
                expectSingleResultinvoke.arg(trueinref)
                fromInvoke.arg(expectSingleResultinvoke)
                val holder = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "e", null)
                val aLambda = JLambda()
                val arr = aLambda.addParam("e")
                val setBody: JBlock = aLambda.body()
                val pollerinvoke= JExpr.invoke(codeModel, holder, "poller")
                setBody.add(pollerinvoke)
                fromInvoke.arg(aLambda)
                val holder11 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "p", null)
                val aLambda11 = JLambda()
                val arr11 = aLambda11.addParam("p")
                val setBody11: JBlock = aLambda11.body()
                val fixedDelayInvoke=JExpr.invoke(codeModel, holder11,"fixedDelay").arg(5000)
                pollerinvoke.arg(aLambda11)
                setBody11.add(fixedDelayInvoke)
                val enrichHeadersInvoke =JExpr.invoke(codeModel,fromInvoke,"enrichHeaders")
                val holder2 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "h", null)
                val aLambda2 = JLambda()
                val arr2= aLambda2.addParam("h")
                val setBody2: JBlock = aLambda2.body()
                setBody2.add(JExpr.invoke(codeModel,holder2,"headerExpression").arg("messageID").arg("headers['id'].toString()"))
                var channelInvoke: JInvocation? =null
                val nextIntegrationNode = nextIntegrationNodes[0]
                when(nextIntegrationNode.instruction){
                    is ComputeInstruction -> {
                        val compute: ComputeInstruction = nextIntegrationNode.instruction as ComputeInstruction
                        val application: Application = compute.configuration as Application
                        channelInvoke = JExpr.invoke(enrichHeadersInvoke.arg(aLambda2), "channel").arg(application.name + "Channel.input")
                    }
                }
                val logInvoke = JExpr.invoke(channelInvoke, "log")
                val getInvoke = JExpr.invoke(logInvoke, "get")
                body._return(getInvoke)

                val className = "school." + eventHandler.name+"PersistenceContext"
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


            }
        }
    }

    override fun outputAdapter(eventHandler: EventHandler, nextIntegrationNodes: List<IntegrationNode>) {
        val adapter = eventHandler.output!!.adapter
        log.info("Generation of the output code for " + adapter)
        when(adapter){
            is OutputFileAdapter -> {
                val outputFileAdapter: OutputFileAdapter = adapter as OutputFileAdapter
            }
            is DatabaseAdapter -> {
                val databaseAdapter: DatabaseAdapter = adapter as DatabaseAdapter
                log.info("Generation of the output code for " + databaseAdapter)
                val method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, eventHandler.name+"Channel")
                method.annotate(Bean::class.java)
                val body =method.body()
                val holder5 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
                val aLambda5 = JLambda()
                val arr5= aLambda5.addParam("f")
                val setBody5: JBlock = aLambda5.body()
                val handleInvoke1 =JExpr.invoke(codeModel,holder5,"handle")
                setBody5.add(handleInvoke1)
                val outboundAdapterInvoke= codeModel.ref(org.springframework.integration.jpa.dsl.Jpa::class.java).staticInvoke("outboundAdapter")
                val thisInvoke1 = JExpr.ref( "this")
                val entityManagerFactoryInvoke1 = JExpr.ref( thisInvoke1,"entityManagerFactory")
                outboundAdapterInvoke.arg(entityManagerFactoryInvoke1)
                val entityClassinvoke1=JExpr.invoke( outboundAdapterInvoke,"entityClass")
                val studentdomainvoke1=JExpr.ref("StudentDomain")
                val classref1=JExpr.ref(studentdomainvoke1,"class")
                entityClassinvoke1.arg(classref1)
                val persistModeinvoke=JExpr.invoke(entityClassinvoke1,"persistMode")
                val PersistModeref=JExpr.ref("PersistMode")
                val PERSISTref=JExpr.ref(PersistModeref,"PERSIST")
                persistModeinvoke.arg(PERSISTref)
                handleInvoke1.arg(persistModeinvoke)
                val holder6 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "e", null)
                val aLambda6 = JLambda()
                val arr6 = aLambda6.addParam("e")
                val setBody6: JBlock = aLambda6.body()
                val transactionInvoke=JExpr.invoke(codeModel, holder6,"transactional")
                handleInvoke1.arg(aLambda6)
                setBody6.add(transactionInvoke)
                val logInvoke1 = JExpr.invoke(codeModel,aLambda5,"log")
                body._return(logInvoke1 )
            }
        }
    }

    override fun filter(expression: String) {
        log.info("Generation of a filter for the expression " + expression)
    }

    override fun serviceActivator(application: Application, nextIntegrationNodes: List<IntegrationNode>) {
        val adapter = application.input!!.adapter
        when(adapter){
            is JavaServiceAdapter -> {

                val javaServiceAdapter: JavaServiceAdapter = adapter as JavaServiceAdapter
                log.info("Generation of a service activator for " + javaServiceAdapter)

                val classe = Class.forName(javaServiceAdapter.javaClass)

                val strings: List<String> = javaServiceAdapter.javaClass.split(".")
                val serviceBeanName = strings[strings.lastIndex].decapitalize()

                var method = generatedClass!!.method(JMod.NONE, classe, serviceBeanName)
                method.annotate(Bean::class.java)   //.param("name","preprocessingForOrchaCompiler")
                var body = method.body()
                val serviceInvoque = JExpr._new(codeModel.ref(classe))
                body._return(serviceInvoque)

                method = generatedClass!!.method(JMod.NONE,  MessageToApplication::class.java, application.name + "MessageToApplication")
                method.annotate(Bean::class.java)
                body = method.body()


                val applicationInvoke=JExpr.ref("Application")
                val stateInvoke=JExpr.refthis(applicationInvoke,"State")
                val terminatedInvoke=JExpr.refthis(stateInvoke,"TERMINATED")

                //val newapreprocessingMessageInvoque = JExpr._new(codeModel.ref( MessageToApplication::class.java)).arg(TERMINATEDInvoke).arg(application.name)
                val messageToApplicationInvoque = JExpr._new(codeModel.ref( MessageToApplication::class.java)).arg(terminatedInvoke).arg(application.name)
                //.arg(Application.State.TERMINATED).arg(postprocessing.name)??????????????????????????????????mech narj3elha

                //  @Bean
                //    MessageToApplication preprocessingMessageToApplication() {
                //        return new MessageToApplication(Application.State.TERMINATED, "preprocessing");
                //    }
                body._return(messageToApplicationInvoque)


                if(applicationToMessageGenerated == false){
                    method.annotate(Bean::class.java)
                    body = method.body()
                    val newapplicationToMessageInvoque = JExpr._new(codeModel.ref( ApplicationToMessage::class.java))
                    body._return(newapplicationToMessageInvoque)
                    applicationToMessageGenerated = true
                }


                method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, application.name + "Channel")
                method.annotate(Bean::class.java)
                body = method.body()

                val holder1 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
                val aLambda1 = JLambda()
                val arr1= aLambda1.addParam("f")
                val setBody1: JBlock = aLambda1.body()

                //val enrichHeadersInvoke =JExpr.invoke(codeModel,holder1,"enrichHeaders")
                //setBody1.add(enrichHeadersInvoke)

                /*val messageToApplicationInvoke=JExpr.invoke(application.name + "MessageToApplication")
                val applicationToMessageInvoke=JExpr.invoke("applicationToMessage")
                val handlerInvoke1=JExpr.invoke(codeModel,aLambda1,"handle").arg("preprocessingForOrchaCompiler").arg("process")
                val handleInvoke2=JExpr.invoke(handlerInvoke1,"handle").arg(messageToApplicationInvoke).arg("transform")
*/

                val messageToApplicationInvoke=JExpr.invoke(application.name + "MessageToApplication")

                val handlerInvoke1=JExpr.invoke(codeModel,holder1,"handle").arg(serviceBeanName).arg(javaServiceAdapter.method)
                setBody1.add(handlerInvoke1)
                val handleInvoke2=JExpr.invoke(codeModel,aLambda1,"handle").arg(messageToApplicationInvoke).arg("transform")

                var channelInvoke: JInvocation? = null

                if(nextIntegrationNodes.size > 0){

                    val nextIntegrationNode = nextIntegrationNodes[0]
                    when(nextIntegrationNode.instruction){
                        is WhenInstruction -> {
                            val whenInstruction: WhenInstruction = nextIntegrationNode.instruction as WhenInstruction
                            channelInvoke = JExpr.invoke(handleInvoke2, "channel").arg("aggregate" + whenInstruction.applicationsOrEventsAsCapitalizedConcatainedString + "Channel.input")
                        }
                    }

                    body._return(channelInvoke)

                } else {
                    body._return(JExpr.invoke(codeModel,aLambda1,"handle"))//.arg(messageToApplicationInvoke).arg("transform"))
                }




            }
        }
    }

    override fun aggregator(whenInstruction: WhenInstruction, nextIntegrationNodes: List<IntegrationNode>) {

        log.info("Generation of the output code for " + whenInstruction)
        val method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, "aggregate" + whenInstruction.applicationsOrEventsAsCapitalizedConcatainedString + "Channel")
        method.annotate(Bean::class.java)
        val body = method.body()
        val holder1 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
        val aLambda1 = JLambda()
        val arr1= aLambda1.addParam("f")
        val aggregateInvoke1 =JExpr.invoke(codeModel,holder1,"aggregate")
        val setBody1: JBlock = aLambda1.body()
        //val aggregateInvoke1 =JExpr.invoke(handleInvoke2,"aggregate")
        val holder3 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "a", null)
        val aLambda3 = JLambda()
        val arr3= aLambda3.addParam("a")
        val setBody3: JBlock = aLambda3.body()
        setBody1.add(aggregateInvoke1)
        //setBody1.add(enrichHeadersInvoke)

        //val relationIdExceptionInvoke=JExpr.invoke(codeModel,holder3,"releaseExpression").arg("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )")
        val relationIdExceptionInvoke=JExpr.invoke(codeModel,holder3,"releaseExpression").arg(whenInstruction.aggregationExpression)
        val correlationStrategyInvoke=JExpr.invoke(relationIdExceptionInvoke,"correlationExpression").arg("headers['messageID']")
        setBody3.add(correlationStrategyInvoke)
        aggregateInvoke1.arg(aLambda3)

        val applicationName = whenInstruction.applicationsOrEvents[0].name
        val transformInvoke=JExpr.invoke(codeModel,aLambda1,"transform").arg("payload.?[name=='" + applicationName + "']")

        //val transformInvoke=JExpr.invoke(codeModel,aLambda1,"transform").arg("payload.?[name=='preprocessing']")
        val applicationToMessageInvoke=JExpr.invoke("applicationToMessage")
        val handlerinvoke =JExpr.invoke(transformInvoke,"handle").arg(applicationToMessageInvoke).arg("transform")

        var channelInvoke: JInvocation? = null

        val nextIntegrationNode = nextIntegrationNodes[0]
        when(nextIntegrationNode.instruction){
            is ComputeInstruction -> {
                val computeInstruction: ComputeInstruction = nextIntegrationNode.instruction as ComputeInstruction
                channelInvoke = JExpr.invoke(handlerinvoke, "channel").arg(computeInstruction.application + "Channel.input")
            }
            is SendInstruction -> {
                val sendInstruction: SendInstruction = nextIntegrationNode.instruction as SendInstruction
                val destination = sendInstruction.destinations[0]
                channelInvoke = JExpr.invoke(handlerinvoke, "channel").arg(destination + "Channel.input")
            }
        }


        //val logInvoke1 = JExpr.invoke(handlerinvoke, "log")
        body._return(channelInvoke)
    }

    override fun getGeneratedCode(): Any {
        return this.codeModel
    }

    /*override fun export(orchaMetadata: OrchaMetadata) {

        val className = orchaMetadata.titleAsCapitalizedConcatainedString + "Application"

        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source" )
        log.info("Export generated class " + className + " to: " + file.absolutePath)
        codeModel.build(FileCodeWriter(file))
    }*/

    override fun export(generatedCode: Any) {

        val codeModel: JCodeModel = generatedCode as JCodeModel

        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source" )
        log.info("Export generated class to: " + file.absolutePath)
        //codeModel.build(FileCodeWriter(file))

        val writer: JCMWriter =  JCMWriter(codeModel)
        writer.setNewLine("\n");
        writer.setCharset(StandardCharsets.UTF_8);

        writer.build(file);

    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputCodeGenerationToSpringIntegrationJavaDSLImpl::class.java)
    }

}