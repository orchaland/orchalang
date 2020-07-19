package transactionJPA;

import orcha.lang.compiler.LexicalAnalysis;
import orcha.lang.compiler.OrchaProgram;
import orcha.lang.compiler.Preprocessing;
import orcha.lang.compiler.referenceimpl.LexicalAnalysisImpl;
import orcha.lang.compiler.referenceimpl.PreprocessingImpl;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.integration.jpa.support.PersistMode;

import javax.persistence.EntityManagerFactory;
import java.io.File;

@SpringBootApplication(scanBasePackages = {"orchalang"})
public class transactionJPAApplication {

    @Value("${orcha.rollback-transaction-directory}")
    String rollbackTransactionDirectory;

    @MessagingGateway
    public interface School {
        @Gateway(requestChannel = "school.input")
        void add(StudentDomain student);
    }

    @Bean(name = "preprocessingForOrchaCompiler")
    Preprocessing preprocessing(){
        return new PreprocessingImpl();
    }

    @Bean
    MessageToApplication preprocessingMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "preprocessing");
    }

    @Bean
    ApplicationToMessage applicationToMessage(){
        return new ApplicationToMessage();
    }

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Bean
    public IntegrationFlow school() {
        return IntegrationFlows
                .from(Jpa.inboundAdapter(this.entityManagerFactory)
                                .entityClass(StudentDomain.class)
                                .maxResults(1)
                                .expectSingleResult(true),
                        e -> e.poller(Pollers.fixedDelay(1000)))
                .channel("preprocessingChannel.input")
                .get();
    }
    @Bean
    public IntegrationFlow preprocessingChannel() {
        return f -> f
                .handle("preprocessingForOrchaCompiler", "process",c -> c.transactional(true))
                .handle(preprocessingMessageToApplication(), "transform")
               .routeToRecipients(r -> r
                .recipient("outputPreprocessingAggregatorChannel.input")
               );
    }
    @Bean
    public DirectChannel outputPreprocessingAggregatorChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outputPreprocessingAggregatorFlow() {
      return  IntegrationFlows.from(outputPreprocessingAggregatorChannel())
              .aggregate(a -> a
              .releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )")
              .correlationExpression("headers['messageID']"))
              .split()
              .filter(String.class, m -> m!="commited transaction")
              .route("payload.?[name=='preprocessing']")
              .get();
    }
    @Bean(name="lexicalAnalysisForOrchaCompiler")
    @DependsOn({"whenInstruction", "sendInstruction"})
    public LexicalAnalysis lexicalAnalysis() {
        return new LexicalAnalysisImpl();
    }

    @Bean
    MessageToApplication lexicalAnalysisMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "lexicalAnalysis");
    }

    @Bean
    public IntegrationFlow lexicalAnalysisChannel() {
        return f -> f
                .handle(Jpa.updatingGateway(this.entityManagerFactory)
                        .entityClass(StudentDomain.class)
                        .persistMode(PersistMode.PERSIST), e -> e.transactional(true))
                .handle("lexicalAnalysisForOrchaCompiler", "analysis")
                .handle(lexicalAnalysisMessageToApplication(), "transform")
                .routeToRecipients(r -> r
                        .recipient("outputlexicalAnalysisAggregatorChannel.input")
                        .recipient("outputChannel")
                );
    }


    @Bean
    public DirectChannel outputRetainingAggregatorChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outputLexicalAnalysisAggregatorFlow() {
        return IntegrationFlows.from(outputRetainingAggregatorChannel())
                .aggregate(a -> a
                        .releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )")
                        .correlationExpression("headers['messageID']"))
                .split()
                .filter(String.class, m -> m!="commited transaction")
                .route("payload.?[name=='lexicalAnalysis']")
                .get();
    }

    @Bean
    public DirectChannel commitedTransactionChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow commitedTransactionFlow() {
        return IntegrationFlows.from(commitedTransactionChannel())
                .split()
                .filter(String.class, m -> m!="commited transaction")
                .route("headers['sendChannel']")
                .get();
    }



    @Bean
    public DirectChannel send_To_orchaProgramDestination() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outputFileFlow() {
        return IntegrationFlows.from(send_To_orchaProgramDestination())
                .enrichHeaders(s -> s.headerExpressions(h -> h.put("messageID", "headers['id'].toString()")))
                .transform(Transformers.toJson())
                .handle(Files.outboundAdapter(new File("." + File.separator + "output1"))
                        .autoCreateDirectory(true))
                .get();
    }

    @Bean
    public DirectChannel outputChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outputChannelFlow() {
        return IntegrationFlows.from(outputChannel())
                .handle("outputGeneration", "generation")
                .log()
                .get();
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(transactionJPAApplication.class).web(WebApplicationType.NONE).run(args);

    }

}