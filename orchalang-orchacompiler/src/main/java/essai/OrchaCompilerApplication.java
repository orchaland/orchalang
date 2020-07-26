package essai;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.*;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;

import java.io.File;

@SpringBootApplication(scanBasePackages = {"orchalang"})
public class OrchaCompilerApplication {

    @Bean
    public IntegrationFlow orchaProgramSourceFlow() {
        return IntegrationFlows.from(Files
                    .inboundAdapter(new File("./files"))
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

    @Bean(name="semanticAnalysisForOrchaCompiler")
    public SemanticAnalysis semanticAnalysis()  {
        return new SemanticAnalysisImpl();
    }

    @Bean(name="postprocessingForOrchaCompiler")
    public Postprocessing postprocessing() {
        return new PostprocessingImpl();
    }

    @Bean
    MessageToApplication preprocessingMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "preprocessing");
    }

    @Bean
    ApplicationToMessage applicationToMessage(){
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow preprocessingChannel() {
        return f -> f
                .enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
                .handle("preprocessingForOrchaCompiler", "process")
                .handle(preprocessingMessageToApplication(), "transform")
                .aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationExpression("headers['messageID']"))
                .transform("payload.?[name=='preprocessing']")
                .handle(applicationToMessage(), "transform")
                .channel("lexicalAnalysisChannel.input");
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
                .handle("lexicalAnalysisForOrchaCompiler", "analysis")
                .handle(lexicalAnalysisMessageToApplication(), "transform")
                .aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationExpression("headers['messageID']"))
                .transform("payload.?[name=='lexicalAnalysis']")
                .handle(applicationToMessage(), "transform")
                .log();
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(OrchaCompilerApplication.class).web(WebApplicationType.NONE).run(args);

    }
}
