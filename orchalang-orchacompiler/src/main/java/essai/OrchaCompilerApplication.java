package essai;

import orcha.lang.compiler.referenceimpl.PreprocessingImpl;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;

import java.io.File;

@SpringBootApplication
public class OrchaCompilerApplication {

    @Bean
    public IntegrationFlow fileReadingFlow() {
        return IntegrationFlows.from(Files.inboundAdapter(new File(".\\files")).patternFilter("*.orcha"), a -> a.poller(Pollers.fixedDelay(1000))).transform(Files.toStringTransformer()).channel("orchaProgramSourceChannel.input").get();
    }

    @Bean
    PreprocessingImpl preprocessing(){
        return new PreprocessingImpl();
    }

    @Bean
    public IntegrationFlow orchaProgramSourceChannel() {
        return f -> f
                .handle("preprocessing", "process")
                .aggregate(a -> a.releaseStrategy(g -> g.size() == 1))
                // handle pour lexicalAnalysis
                // aggregate pour lexicalAnalysis
                .log();
    }

    public static void main(String[] args) {

        new SpringApplicationBuilder(OrchaCompilerApplication.class).web(WebApplicationType.NONE).run(args);

    }
}
