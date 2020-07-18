package transactionJPA;

import essai.OrchaCompilerApplication;
import orcha.lang.compiler.Preprocessing;
import orcha.lang.compiler.SyntaxAnalysis;
import orcha.lang.compiler.referenceimpl.PreprocessingImpl;
import orcha.lang.compiler.referenceimpl.SyntaxAnalysisImpl;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
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
import java.util.List;

@SpringBootApplication
@IntegrationComponentScan
public class transactionJPAApplication {


    @Value("${orcha.rollback-transaction-directory}")
    String rollbackTransactionDirectory;


    @Bean
    public IntegrationFlow orchaProgramSourceFlow() {
        return IntegrationFlows.from(Files
                .inboundAdapter(new File("./file1"))
                .patternFilter("*.orcha"), a -> a.poller(Pollers.fixedDelay(1000)))
                .channel("preprocessingChannel.input")
                .get();
    }
    @Bean(name = "preprocessingForOrchaCompiler")
    Preprocessing preprocessing(){
        return new PreprocessingImpl();
    }
    @Bean(name="syntaxAnalysisForOrchaCompiler")
    public SyntaxAnalysis syntaxAnalysis() {
        return new SyntaxAnalysisImpl();
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
    public IntegrationFlow preprocessingChannel() {
        return f -> f
                .handle(Jpa.updatingGateway(this.entityManagerFactory)
                        .entityClass(Preprocessing.class)
                        .persistMode(PersistMode.PERSIST), e -> e.transactional(true))
                .handle("preprocessing", "process")
                .handle(preprocessingMessageToApplication(), "transform")
                .aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationExpression("headers['messageID']"))
                .transform("payload.?[name=='preprocessing']")
                .handle(applicationToMessage(), "transform")
                .channel("lexicalAnalysisChannel.input");
    }


    @Bean
    public IntegrationFlow lexicalAnalysisChannel() {
        return f -> f
                .handle("lexicalAnalysisForOrchaCompiler", "analysis", c -> c.transactional(true))
                .enrichHeaders(h -> h.header("sendChannel", "send_To_orchaProgramDestination"))
                .routeToRecipients(r -> r
                        .recipient("outputRetainingAggregatorChannel")
                        .recipient("outputChannel")
                );
    }
    @Bean
    public DirectChannel outputRetainingAggregatorChannel() {
        return new DirectChannel();
    }
    @Bean
    public IntegrationFlow outputRetainingAggregatorFlow() {
        return IntegrationFlows.from(outputRetainingAggregatorChannel())
                .aggregate(a -> a
                        .releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )")
                        .correlationExpression("headers['messageID']"))
                .split()
                .filter(String.class, m -> m!="commited transaction")
                .route("headers['sendChannel']")
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
                .enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
                .transform(Transformers.toJson())
                .handle(Files.outboundAdapter(new File("." + File.separator + "output1")).autoCreateDirectory(true))
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