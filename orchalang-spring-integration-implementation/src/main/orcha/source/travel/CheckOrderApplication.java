package travel;

import java.io.File;
import orcha.lang.compiler.referenceimpl.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.MessageToApplication;
import orcha.lang.compiler.referenceimpl.PreprocessingImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.jpa.dsl.Jpa;

@SpringBootApplication(scanBasePackages = {
    "travel"
})
public class CheckOrderApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CheckOrderApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public IntegrationFlow travelAgencyFlow() {
        return IntegrationFlows.from(Files.inboundAdapter(new File("./files")).patternFilter("*.json"), a -> a.poller(Pollers.fixedDelay(1000))).enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).channel("checkOrderChannel.input").get();
    }

    @Bean
    PreprocessingImpl preprocessingImpl() {
        return new PreprocessingImpl();
    }

    @Bean
    MessageToApplication checkOrderMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "checkOrder");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow checkOrderChannel() {
        return f -> f.handle("preprocessingImpl", "process").handle(checkOrderMessageToApplication(), "transform").channel("aggregateCheckOrderChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateCheckOrderChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (Application [state=TERMINATES, name=checkOrder, condition=null, order=1])").correlationStrategy("headers['messageID']")).transform("payload.?[name=='checkOrder']").handle(applicationToMessage(), "transform").channel("customerChannel.input");
    }

    @Bean
    public IntegrationFlow customerChannel() {
        return f -> f.handle(Jpa.outboundAdapter(this.entityManagerFactory).entityClass(StudentDomain.class).persistMode(PersistMode.PERSIST), e -> e.transactional()).log();
    }
}
