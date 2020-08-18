package mongodb;


import com.mongodb.client.MongoClients;

import orcha.lang.compiler.LexicalAnalysis;
import orcha.lang.compiler.Postprocessing;
import orcha.lang.compiler.SemanticAnalysis;
import orcha.lang.compiler.SyntaxAnalysis;
import orcha.lang.compiler.referenceimpl.*;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.MongoDbFactory;

import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mongodb.dsl.MongoDb;
import org.springframework.integration.mongodb.outbound.MongoDbStoringMessageHandler;
import org.springframework.messaging.MessageHandler;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class MongodbApplication {
    @Bean
    PreprocessingImpl preprocessingImpl(){
        return new PreprocessingImpl();
    }

    @Bean(name="syntaxAnalysisForOrchaCompiler")
    public SyntaxAnalysis syntaxAnalysis() {
        return new SyntaxAnalysisImpl();
    }

    @Bean(name="semanticAnalysisForOrchaCompiler")
    public SemanticAnalysis semanticAnalysis()  {
        return new SemanticAnalysisImpl();
    }

    @Bean(name="postprocessingForOrchaCompiler")
    public Postprocessing postprocessing() {
        return new PostprocessingImpl();
    }
    @Bean(name="lexicalAnalysisForOrchaCompiler")
    @DependsOn({"whenInstruction", "sendInstruction"})
    public LexicalAnalysis lexicalAnalysis() {
        return new LexicalAnalysisImpl();
    }


    @Bean
    MongoDbFactory mongoDbFactory(){
        return new SimpleMongoClientDbFactory(MongoClients.create(), "test");
    }

    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Autowired
    private MongoConverter mongoConverter;

    @Bean
    public IntegrationFlow gatewaySingleQueryFlow() {
        return f -> f
                .handle(MongoDb.outboundGateway(mongoDbFactory(), this.mongoConverter)
                        .query("{firstName : 'Marwa'}")
                        .collectionNameExpression("'student'")
                        .expectSingleResult(true)
                        .entityClass(StudentDomain.class))
                .enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
                .channel("enrollStudentChannel.input")
                .log();
    }

    @Bean(name = "enrollStudent")
    EnrollStudent enrollStudent() {
        return new EnrollStudent();
    }

    @Bean
    MessageToApplication enrollStudentMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "enrollStudent");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow enrollStudentChannel() {
        return f -> f.handle("enrollStudent", "enroll").handle(enrollStudentMessageToApplication(), "transform").channel("aggregateEnrollStudentChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateEnrollStudentChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationExpression("headers['messageID']")).transform("payload.?[name=='enrollStudent']").handle(applicationToMessage(), "transform").channel("studentOutputDatabaseChannel.input");
    }


    @Bean
    public IntegrationFlow studentOutputDatabaseChannel() {
        return f -> f
                .handle(mongoOutboundAdapter(mongoDbFactory()));
    }

    @Bean
    @Autowired
    public MessageHandler mongoOutboundAdapter(MongoDbFactory mongo) {
        MongoDbStoringMessageHandler mongoHandler = new MongoDbStoringMessageHandler(mongo);
        mongoHandler.setCollectionNameExpression(new LiteralExpression("student"));
        return mongoHandler;
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(MongodbApplication.class, args);
        PopulateDatabase populateDatabase = (PopulateDatabase) context.getBean("populateDatabase");
        Flux<StudentDomain> results;
      /*  School school = context.getBean(School.class);
        StudentDomain studentDomain = new StudentDomain("Lineda", 40, -1);
        school.placeOrder(studentDomain);*/

        /*PopulateDatabase populateDatabase = (PopulateDatabase) context.getBean("populateDatabase");
        //List<?> results = populateDatabase.readDatabase();
        Flux<StudentDomain> results;*/

        System.out.println("\nmanyStudentsInValideTransaction is starting\n");
        try {

            StudentDomain student = new StudentDomain("marwa", 40, -1);
            populateDatabase.saveStudent(student);
            results = populateDatabase.readDatabase();


        } catch (Exception e) {
            System.out.println(">>>>>> Caught exception: " + e);
        }

        //results = populateDatabase.readDatabase();
        //System.out.println("database: " + results);
        //List<?> results = populateDatabase.readDatabase();*/


    }
}
