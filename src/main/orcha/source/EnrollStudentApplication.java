import com.example.jpa.EnrollStudent;
import javax.persistence.EntityManagerFactory;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jpa.dsl.Jpa;

@SpringBootApplication
public class EnrollStudentApplication {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
        return;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(OrchaCompilerApplication.class).web(WebApplicationType.NONE).run(args);
    }
}
