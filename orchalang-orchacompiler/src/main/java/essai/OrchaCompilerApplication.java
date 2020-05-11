package essai;

import orcha.lang.compiler.referenceimpl.*;
import orcha.lang.compiler.referenceimpl.springIntegration.LinkEditorImpl;
import orcha.lang.compiler.referenceimpl.springIntegration.OutputGenerationToSpringIntegration;
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
    PreprocessingImpl preprocessing(){ return new PreprocessingImpl(); }

    @Bean
    LexicalAnalysisImpl lexicalAnalysis(){return new  LexicalAnalysisImpl();}

    @Bean
    SyntaxAnalysisImpl syntaxAnalysis(){return new  SyntaxAnalysisImpl();}

    @Bean
    SemanticAnalysisImpl semanticAnalysis(){return new   SemanticAnalysisImpl();}

    @Bean
    LinkEditorImpl linkEditor(){return new  LinkEditorImpl();}

    @Bean
    OutputGenerationToSpringIntegration outputGeneration(){return new  OutputGenerationToSpringIntegration();}

    @Bean
    public IntegrationFlow orchaProgramSourceChannel() {
        return f -> f
                .handle("preprocessing", "process")
                .aggregate(a -> a.releaseStrategy(g -> g.size() == 1))
                .handle("lexicalAnalysis", "analysis")
                .aggregate(a -> a.releaseStrategy(g -> g.size() == 1))
                .handle("syntaxAnalysis", "analysis")
                .aggregate(a -> a.releaseStrategy(g -> g.size() == 1))
                .handle("semanticAnalysis", "analysis")
                .aggregate(a -> a.releaseStrategy(g -> g.size() == 1))
                .handle("linkEditor", "link")
                .aggregate(a -> a.releaseStrategy(g -> g.size() == 1))
                .handle("outputGeneration", "generation")
                .aggregate(a -> a.releaseStrategy(g -> g.size() == 1))
                // send outputGeneration.result to orchaProgramDestination ??????????????????????????????????
                //?????????????????????????????????????
               // .handle("outputGeneration","orchaProgramDestination")





                .log();
    }

    public static void main(String[] args) {

        new SpringApplicationBuilder(OrchaCompilerApplication.class).web(WebApplicationType.NONE).run(args);

    }
}
