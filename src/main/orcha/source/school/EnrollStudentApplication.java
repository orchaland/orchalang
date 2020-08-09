package school;

import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;

@SpringBootApplication(scanBasePackages = {
    "school"
})
public class EnrollStudentApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(EnrollStudentApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
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
}
