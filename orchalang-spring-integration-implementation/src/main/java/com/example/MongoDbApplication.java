package com.example;

import com.example.mongodb.EnrollStudent;
import com.example.mongodb.InfrastructureConfiguration;
import com.example.mongodb.StudentDomain;
import com.example.mongodb.StudentRepository;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.*;
import org.springframework.integration.mongodb.dsl.MongoDb;
import org.springframework.integration.mongodb.dsl.MongoDbOutboundGatewaySpec;
import org.springframework.integration.scheduling.PollerMetadata;
@SpringBootApplication
@EnableIntegration
public class MongoDbApplication {

   @Autowired
    private MongoDbFactory mongoDbFactory;

    @Autowired
    private MongoConverter mongoConverter;


    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedDelay(1000).get();
    }

    @Bean
    IntegrationFlow studentDatabaseFlow(ReactiveMongoOperations mongoTemplate) {
        return IntegrationFlows.from(
                MongoDb.changeStreamInboundChannelAdapter(mongoTemplate)
                        .domainType(StudentDomain.class)
                        .collection("student")
                        .extractBody(false))
                .enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
                .channel("enrollStudentChannel.input")
                .get();
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
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))")
                .correlationExpression("headers['messageID']"))
                .transform("payload.?[name=='enrollStudent']").handle(applicationToMessage(), "transform").channel("studentOutputDatabaseChannel.input");
    }



    private MongoDbOutboundGatewaySpec queryOutboundGateway() {
        return MongoDb.outboundGateway(this.mongoDbFactory, this.mongoConverter)
                .query("{firstName  : 'marwa'}")
                .collectionNameFunction(m -> m.getHeaders().get("collection"))
                .expectSingleResult(true)
                .entityClass(StudentDomain.class);
    }

    @Bean
    public IntegrationFlow studentOutputDatabaseChannel() {
        return f -> f
                .handle(queryOutboundGateway())
                .channel(c -> c.queue("retrieveResults"));
    }



        public static void main(String[] args) {
            ConfigurableApplicationContext context = SpringApplication.run(MongoDbApplication.class, args);
            new MongoDbApplication().start(context);
        }

        public void start(ConfigurableApplicationContext context) {
            resetDatabase(context);
            StudentDomain student1 = new StudentDomain("marwa", 25,1);
            StudentDomain student2= new StudentDomain("sawssen", 27,2);
            StudentDomain student3= new StudentDomain("dorsaf", 23,3);

            InfrastructureConfiguration.StudentService studentService = context.getBean(InfrastructureConfiguration.StudentService.class);

            studentService.student(student1);
            studentService.student(student2);
            studentService.student(student3);
        }

        private void resetDatabase(ConfigurableApplicationContext context) {
            StudentRepository studentRepository = context.getBean(StudentRepository.class);
            studentRepository.deleteAll();
        }
    }

