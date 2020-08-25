package orcha.lang.compiler.referenceimpl.springIntegration.javaDSLgenerator

import com.helger.jcodemodel.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.integration.dsl.IntegrationFlow
import java.io.File



class dataGeneration {
    fun generate() {
        val codeModel = JCodeModel()
        val className = "schoolMongodb." + "MongodbReadWriteApplication"
        val generatedClass = codeModel._class(JMod.PUBLIC, className, EClassType.CLASS)
        generatedClass.annotate(SpringBootApplication::class.java)
        //  @Bean
        //    MongoDatabaseFactory mongoDbFactory(){
        //        return new SimpleMongoClientDatabaseFactory(MongoClients.create(), "test");
        //    }
        var method = generatedClass.method(JMod.NONE, MongoDatabaseFactory::class.java, "mongoDbFactory")
        method.annotate(Bean::class.java)
        var body = method.body()
        val MongoClientsInvoke=JExpr.ref("MongoClients")
        val createInvoke=JExpr.invoke(MongoClientsInvoke,"create")
        val newaenrollStudentMessageInvoque = JExpr._new(codeModel.ref(SimpleMongoClientDatabaseFactory::class.java)).arg(createInvoke).arg("test")
        body._return(newaenrollStudentMessageInvoque)
        // @Autowired
        //    private MongoConverter mongoConverter;
        val mongoConverter: JFieldVar = generatedClass.field(JMod.PRIVATE, MongoConverter::class.java, "mongoConverter")
        mongoConverter.annotate(Autowired::class.java)
        //@Bean
        //    public IntegrationFlow studentDatabaseFlow() {
        //        return f -> f
        //                .handle(queryOutboundGateway());
        //    }
        method = generatedClass.method(JMod.PUBLIC, IntegrationFlow::class.java, "studentDatabaseFlow")
        method.annotate(Bean::class.java)
        body =method.body()
        val holder = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
        val aLambda = JLambda()
        val arr= aLambda.addParam("f")
        val setBody: JBlock = aLambda.body()
        val queryOutboundinvok =JExpr.invoke("queryOutboundGateway")
        val handleInvoke1 =JExpr.invoke(codeModel,holder,"handle").arg(queryOutboundinvok)
       setBody.add(handleInvoke1)
         body._return(aLambda)

        //@Bean
        //    public IntegrationFlow queryOutboundGateway() {
        //        return f -> f
        //                .handle(MongoDb.outboundGateway(mongoDbFactory(), this.mongoConverter)
        //                        .query("{firstName : 'Marwa'}")
        //                        .collectionNameExpression("student")
        //                        .expectSingleResult(true)
        //                        .entityClass(StudentDomain.class))
        //                .enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
        //                .channel("enrollStudentChannel.input")
        //                .log();
        //    }
        method = generatedClass.method(JMod.PUBLIC, IntegrationFlow::class.java, "queryOutboundGateway")
        method.annotate(Bean::class.java)
        body =method.body()
        val holder1 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
        val aLambda1 = JLambda()
        val arr1= aLambda1.addParam("f")
        val setBody1: JBlock = aLambda1.body()
        val handleInvoke =JExpr.invoke(codeModel,holder1,"handle")
        setBody1.add(handleInvoke)
        val thisInvoke1 = JExpr.ref( "this")
        val entityManagerFactoryInvoke1 = JExpr.ref( thisInvoke1,"mongoConverter")
        val outboundAdapterInvoke= codeModel.ref(org.springframework.integration.mongodb.dsl.MongoDb::class.java).staticInvoke("outboundGateway").arg(mongoConverter).arg(entityManagerFactoryInvoke1)
        val queryinvoke1=JExpr.invoke( outboundAdapterInvoke,"query").arg("{firstName : 'Marwa'}")
        val persistModeinvoke=JExpr.invoke(queryinvoke1,"collectionNameExpression").arg("student")
        val expectSingleResultinvoke=JExpr.invoke(persistModeinvoke,"expectSingleResult").arg(true)
        val studentdomainvoke1=JExpr.ref("StudentDomain")
        val classref1=JExpr.ref(studentdomainvoke1,"class")
        val entityClassinvoke1=JExpr.invoke( expectSingleResultinvoke,"entityClass").arg(classref1)
        handleInvoke.arg(entityClassinvoke1)

        val enrichHeadersInvoke =JExpr.invoke(codeModel,aLambda1,"enrichHeaders")
        val holder2 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "h", null)
        val aLambda2 = JLambda()
        val arr2= aLambda2.addParam("h")
        val setBody2: JBlock = aLambda2.body()
        setBody2.add(JExpr.invoke(codeModel,holder2,"headerExpression").arg("messageID").arg("headers['id'].toString()"))
        val channelInvoke = JExpr.invoke(enrichHeadersInvoke.arg(aLambda2), "channel").arg("enrollStudentChannel.input")
        val logInvoke = JExpr.invoke(channelInvoke, "log")
        body._return(logInvoke)
        //  @Bean
        //    public IntegrationFlow studentOutputDatabaseChannel() {
        //        return f -> f
        //                .handle(mongoOutboundAdapter(mongoDbFactory()));
        //    }
        method = generatedClass.method(JMod.PUBLIC, IntegrationFlow::class.java, "studentOutputDatabaseChannel")
        method.annotate(Bean::class.java)
        body =method.body()
        val holder3 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
        val aLambda3 = JLambda()
        val arr3= aLambda3.addParam("f")
        val setBody3: JBlock = aLambda3.body()
        val mongoDbFactory=JExpr.invoke("mongoDbFactory")
        val mongoOutboundAdapterinvok =JExpr.invoke("mongoOutboundAdapter").arg(mongoDbFactory)
        val handleInvoke3 =JExpr.invoke(codeModel,holder3,"handle").arg(mongoOutboundAdapterinvok)
        setBody3.add(handleInvoke3)
        body._return(aLambda3)

        //@Bean
        //    @Autowired
        //    public MessageHandler mongoOutboundAdapter(MongoDatabaseFactory mongo) {
        //        MongoDbStoringMessageHandler mongoHandler = new MongoDbStoringMessageHandler(mongo);
        //        mongoHandler.setCollectionNameExpression(new LiteralExpression("student"));
        //        return mongoHandler;
        //    }



//    public static void main(String[] args) {
//
//        ConfigurableApplicationContext context = SpringApplication.run(MongodbReadWriteApplication.class, args);
//        PopulateDatabase populateDatabase = (PopulateDatabase) context.getBean("populateDatabase");
//        StudentDomain student = new StudentDomain("Marwa", 40, -1);
//        populateDatabase.saveStudent(student);
//        populateDatabase.readDatabase();
//        System.out.println("database: " +  populateDatabase.readDatabase());
//
//
//    }
        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source")
        codeModel.build(file)


    }
}
