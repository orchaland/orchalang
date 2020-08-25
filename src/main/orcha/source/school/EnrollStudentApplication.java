package school;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jpa.dsl.Jpa;

@SpringBootApplication(scanBasePackages = {
    "school"
})
public class EnrollStudentApplication {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(JPAApplication.class, args);
        PopulateDatabase populateDatabase =  context.getBean("populateDatabase");
        List results;
        try {
            StudentDomain student = new StudentDomain("Morgane", 21, -1);
            populateDatabase.saveStudent(student);
            results = populateDatabase.readDatabase();
            System.out.println("database:", results);
        } catch (final Exception e) {
            System.out.println(">>>>>> Caught exception:+", e);
        }
    }

    @Bean
    public IntegrationFlow studentDatabaseFlow() {
        return IntegrationFlows.from(Jpa.inboundAdapter(this.entityManagerFactory).entityClass(StudentDomain.class).maxResults(1).expectSingleResult(true), e -> e.poller(p -> p.fixedDelay(5000))).enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).channel("enrollStudentChannel.input").log().get();
    }

    @Bean
    EnrollStudent enrollStudent() {
        return new EnrollStudent();
    }

    @Bean
    @Bean
    MessageToApplication enrollStudentMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "enrollStudent");
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow enrollStudentChannel() {
        return f -> f.handle("enrollStudent", "enroll").handle(enrollStudentMessageToApplication(), "transform").channel("aggregateEnrollStudentChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateEnrollStudentChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationExpression("headers['messageID']")).transform("payload.?[name=='enrollStudent']").handle(applicationToMessage(), "transform").channel("studentDatabaseChannel.input");
    }

    @Bean
    public IntegrationFlow studentDatabaseChannel() {
        return f -> f.handle(Jpa.outboundAdapter(this.entityManagerFactory).entityClass(StudentDomain.class).persistMode(PersistMode.PERSIST), e -> e.transactional()).log();
    }
}
