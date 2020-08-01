package com.example.generationessai;

import com.example.jpa.EnrollStudent;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;

@SpringBootApplication
public class OrchaCompilerApplication {
    @Autowired
    private EntityManagerFactoryInfo entityManagerFactory;
    ConfigurableApplicationContext context;

    @Bean
    public IntegrationFlow studentDatabaseFlow() {
        return IntegrationFlows.from(Jpa.inboundAdapter(this.entityManagerFactory).entityClass(StudentDomain.class).maxResults(1).expectSingleResult(true), e -> e.poller(p -> p.fixedDelay(5000))).enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).channel("enrollStudentChannel.input").log().get();
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
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='enrollStudent']").handle(applicationToMessage(), "transform").channel("studentOutputDatabaseChannel.input");
    }

    @Bean
    public IntegrationFlow studentOutputDatabaseChannel() {
        return f -> f.handle(Jpa.outboundAdapter(this.entityManagerFactory).entityClass(StudentDomain.class).persistMode(PersistMode.PERSIST), e -> e.transactional()).log();
    }

    public static void main(String[] args) {
        return context;
    }
}
