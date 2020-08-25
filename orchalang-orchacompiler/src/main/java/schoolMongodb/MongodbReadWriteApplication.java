package schoolMongodb;

import com.mongodb.client.MongoClients;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mongodb.dsl.MongoDb;
import org.springframework.integration.mongodb.outbound.MongoDbStoringMessageHandler;
import org.springframework.messaging.MessageHandler;

public class MongodbReadWriteApplication {
    @Bean
    MongoDatabaseFactory mongoDbFactory(){
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(), "test");
    }

    @Autowired
    private MongoConverter mongoConverter;
    @Bean
    public IntegrationFlow studentDatabaseFlow() {
        return f -> f
                .handle(queryOutboundGateway());
    }
    @Bean
    public IntegrationFlow queryOutboundGateway() {
        return f -> f
                .handle(MongoDb.outboundGateway(mongoDbFactory(), this.mongoConverter)
                        .query("{firstName : 'Marwa'}")
                        .collectionNameExpression("student")
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
    public MessageHandler mongoOutboundAdapter(MongoDatabaseFactory mongo) {
        MongoDbStoringMessageHandler mongoHandler = new MongoDbStoringMessageHandler(mongo);
        mongoHandler.setCollectionNameExpression(new LiteralExpression("student"));
        return mongoHandler;
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(MongodbReadWriteApplication.class, args);
        PopulateDatabase populateDatabase = (PopulateDatabase) context.getBean("populateDatabase");
        StudentDomain student = new StudentDomain("Marwa", 40, -1);
        populateDatabase.saveStudent(student);
        populateDatabase.readDatabase();
        System.out.println("database: " +  populateDatabase.readDatabase());


    }
}
