package orcha.lang.compiler.referenceimpl.springIntegration.javaDSLgenerator

import com.example.jpa.EnrollStudent
import com.helger.jcodemodel.*
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import org.springframework.orm.jpa.EntityManagerFactoryInfo
import java.io.File


class essaiGeneration {
    fun generate() {

        // @Bean
        //    public IntegrationFlow studentDatabaseFlow() {
        //        return IntegrationFlows
        //                .from(Jpa.inboundAdapter(this.entityManagerFactory)
        //                                .entityClass(StudentDomain.class)
        //                                .maxResults(1)
        //                                .expectSingleResult(true),
        //                        e -> e.poller(p -> p.fixedDelay(5000)))
        //                .enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
        //                .channel("enrollStudentChannel.input")
        //                .log()
        //                .get();
        //    }
        val codeModel = JCodeModel()
        val className = "com.example.generationessai." + "OrchaCompilerApplication"
        val generatedClass = codeModel._class(JMod.PUBLIC, className, EClassType.CLASS)
        generatedClass.annotate(SpringBootApplication::class.java)

        val entityManagerFactory: JFieldVar = generatedClass.field(JMod.PRIVATE,EntityManagerFactoryInfo::class.java, "entityManagerFactory")
        entityManagerFactory.annotate(Autowired::class.java)
        var method = generatedClass.method(JMod.PUBLIC, IntegrationFlow::class.java, "studentDatabaseFlow")
        method.annotate(Bean::class.java)
        var body =method.body()

     val fromInvoke = codeModel.ref(IntegrationFlows::class.java).staticInvoke("from")
        val inboundAdapterInvoke: JInvocation = codeModel.ref(org.springframework.integration.jpa.dsl.Jpa::class.java).staticInvoke("inboundAdapter")
        val thisInvoke = JExpr.ref( "this")
        val entityManagerFactoryInvoke = JExpr.ref( thisInvoke,"entityManagerFactory")
        inboundAdapterInvoke.arg(entityManagerFactoryInvoke)

        val entityClassinvoke=JExpr.invoke(inboundAdapterInvoke,"entityClass")
        val studentdomainref=JExpr.ref("StudentDomain")
        val classref=JExpr.ref(studentdomainref,"class")
        entityClassinvoke.arg(classref)
      //.maxResults(1)
        //.expectSingleResult(true),

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
        val channelInvoke = JExpr.invoke(enrichHeadersInvoke.arg(aLambda2), "channel").arg("enrollStudentChannel.input")
        val logInvoke = JExpr.invoke(channelInvoke, "log")
        val getInvoke = JExpr.invoke(logInvoke, "get")
        //.log()
        // .get();
        body._return(getInvoke)

        /*  @Bean(name = "enrollStudent")
    EnrollStudent enrollStudent() {
        return new EnrollStudent();
    }*/
        method = generatedClass.method(JMod.NONE,EnrollStudent::class.java,"enrollStudent")
        method.annotate(Bean::class.java).param("name", "enrollStudent" )
          body = method.body()
        val newEnrollStudentInvoque = JExpr._new(codeModel.ref(EnrollStudent::class.java))
        body._return(newEnrollStudentInvoque)
//@Bean
//    MessageToApplication enrollStudentMessageToApplication() {
//        return new MessageToApplication(Application.State.TERMINATED, "enrollStudent");
//    }
        method = generatedClass!!.method(JMod.NONE,  MessageToApplication::class.java,"enrollStudentMessageToApplication" )
        method.annotate(Bean::class.java)
        body = method.body()
        val ApplicationInvoke=JExpr.ref("Application")
        val StateInvoke=JExpr.refthis(ApplicationInvoke,"State")
        val TERMINATEDInvoke=JExpr.refthis(StateInvoke,"TERMINATED")
        val newaenrollStudentMessageInvoque = JExpr._new(codeModel.ref( MessageToApplication::class.java)).arg(TERMINATEDInvoke).arg("enrollStudent")
        body._return(newaenrollStudentMessageInvoque)

        // @Bean
        //    ApplicationToMessage applicationToMessage() {
        //        return new ApplicationToMessage();
        //    }
        method = generatedClass!!.method(JMod.NONE,  ApplicationToMessage::class.java, "applicationToMessage")
        method.annotate(Bean::class.java)
        body = method.body()
        val newapplicationToMessageInvoque = JExpr._new(codeModel.ref( ApplicationToMessage::class.java))
        body._return(newapplicationToMessageInvoque)
        // @Bean
        //    public IntegrationFlow enrollStudentChannel() {
        //        return f -> f.handle("enrollStudent", "enroll").handle(enrollStudentMessageToApplication(), "transform").channel("aggregateEnrollStudentChannel.input");
        //    }

        method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java,  "enrollStudentChannel")
        method.annotate(Bean::class.java)
        body = method.body()
        val holder1 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
        val aLambda1 = JLambda()
        val arr1= aLambda1.addParam("f")
        val setBody1: JBlock = aLambda1.body()
        val messageToApplicationInvoke=JExpr.invoke("enrollStudentMessageToApplication")
        val handlerInvoke1=JExpr.invoke(codeModel,holder1,"handle").arg("enrollStudent").arg("enroll")
        setBody1.add(handlerInvoke1)
        val handleInvoke2=JExpr.invoke(codeModel,aLambda1,"handle").arg(messageToApplicationInvoke).arg("transform")
        val channelInvoke1 = JExpr.invoke(handleInvoke2, "channel").arg("aggregateEnrollStudentChannel.input")
        body._return(channelInvoke1)

         method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, "aggregateEnrollStudentChannel")
        method.annotate(Bean::class.java)
         body = method.body()
        val holder4 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
        val aLambda4 = JLambda()
        val arr4= aLambda4.addParam("f")
        val aggregateInvoke1 =JExpr.invoke(codeModel,holder4,"aggregate")
        val setBody4: JBlock = aLambda4.body()
        val holder3 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "a", null)
        val aLambda3 = JLambda()
        val arr3= aLambda3.addParam("a")
        val setBody3: JBlock = aLambda3.body()
        setBody4.add(aggregateInvoke1)
        val relationIdExceptionInvoke=JExpr.invoke(codeModel,holder3,"releaseExpression").arg("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))")
        val correlationStrategyInvoke=JExpr.invoke(relationIdExceptionInvoke,"correlationStrategy").arg("headers['messageID']")
        setBody3.add(correlationStrategyInvoke)
        aggregateInvoke1.arg(aLambda3)
        val transformInvoke=JExpr.invoke(codeModel,aLambda4,"transform").arg("payload.?[name=='enrollStudent']")
        val applicationToMessageInvoke=JExpr.invoke("applicationToMessage")
        val handlerinvoke =JExpr.invoke(transformInvoke,"handle").arg(applicationToMessageInvoke).arg("transform")
        val channelInvoke3 = JExpr.invoke(handlerinvoke, "channel").arg("studentOutputDatabaseChannel.input")
        body._return(channelInvoke3)
        // @Bean
        //    public IntegrationFlow studentOutputDatabaseChannel() {
        //        return f -> f
        //                .handle(Jpa.outboundAdapter(this.entityManagerFactory)
        //                                .entityClass(StudentDomain.class)
        //                                .persistMode(PersistMode.PERSIST),
        //                        e -> e.transactional())
        //                .log();
        //    }

        method = generatedClass.method(JMod.PUBLIC, IntegrationFlow::class.java, "studentOutputDatabaseChannel")
        method.annotate(Bean::class.java)
        body =method.body()
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
    //setBody6.add(JExpr.invoke(codeModel, holder6, "transactional"))
        val transactionInvoke=JExpr.invoke(codeModel, holder6,"transactional")
        //handleInvoke1.arg(transactionInvoke)
        handleInvoke1.arg(aLambda6)
        setBody6.add(transactionInvoke)

        val logInvoke1 = JExpr.invoke(codeModel,aLambda5,"log")
               body._return(logInvoke1 )


/* public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(JPAApplication.class, args);
        PopulateDatabase populateDatabase = (PopulateDatabase) context.getBean("populateDatabase");
        //List<?> results = populateDatabase.readDatabase();
        List<?> results;
        System.out.println("\nmanyStudentsInValideTransaction is starting\n");
        try {
            StudentDomain student = new StudentDomain("Morgane", 21, -1);
            populateDatabase.saveStudent(student);
            results = populateDatabase.readDatabase();
            System.out.println("database: " + results);
        } catch (Exception e) {
            System.out.println(">>>>>> Caught exception: " + e);
        }
        //results = populateDatabase.readDatabase();
        //System.out.println("database: " + results);
        //List<?> results = populateDatabase.readDatabase();
    }*/
        method = generatedClass!!.method(JMod.PUBLIC or JMod.STATIC, codeModel.VOID, "main")

        val springRef = codeModel.ref("String")
        method.param(JMod.NONE, springRef.array(), "args")
         body = method.body()
//ConfigurableApplicationContext context = SpringApplication.run(JPAApplication.class, args);
        val configurableApplicationContextinvoker: JFieldVar = generatedClass.field(JMod.NONE,ConfigurableApplicationContext::class.java, "context")
        val springApplicationinvoke1=JExpr.ref("SpringApplication")
        val orchaInvoke = JExpr.ref("JPAApplication")
        val classInvoke = JExpr.refthis(orchaInvoke, "class")
        val argsref1=JExpr.ref("args")
        val runinvok = JExpr.invoke(springApplicationinvoke1,"run").arg(classInvoke).arg(argsref1)
        val equll= configurableApplicationContextinvoker.assign(runinvok)
        body._return(equll)



        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source")
        codeModel.build(file)


    }
}
