import java.io.File;
import orcha.lang.compiler.referenceimpl.LexicalAnalysisImpl;
import orcha.lang.compiler.referenceimpl.PostprocessingImpl;
import orcha.lang.compiler.referenceimpl.PreprocessingImpl;
import orcha.lang.compiler.referenceimpl.SemanticAnalysisImpl;
import orcha.lang.compiler.referenceimpl.SyntaxAnalysisImpl;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.LinkEditorImpl;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.compiler.referenceimpl.springIntegration.OutputGenerationImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;

@SpringBootApplication
public class OrchaCompilerApplication {

    @Bean
    public IntegrationFlow orchaProgramSourceFlow() {
        return IntegrationFlows.from(Files.inboundAdapter(new File("./files")).patternFilter("*.orcha"), a -> a.poller(Pollers.fixedDelay(1000))).enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).channel("preprocessingChannel.input").get();
    }

    @Bean(name = "preprocessing")
    PreprocessingImpl preprocessing() {
        return new PreprocessingImpl();
    }

    @Bean
    MessageToApplication preprocessingMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "preprocessing");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow preprocessingChannel() {
        return f -> f.handle("preprocessing", "process").handle(preprocessingMessageToApplication(), "transform").channel("aggregatePreprocessingChannel.input");
    }

    @Bean
    public IntegrationFlow aggregatePreprocessingChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='preprocessing']").handle(applicationToMessage(), "transform").channel("lexicalAnalysisChannel.input");
    }

    @Bean(name = "lexicalAnalysis")
    LexicalAnalysisImpl lexicalAnalysis() {
        return new LexicalAnalysisImpl();
    }

    @Bean
    MessageToApplication lexicalAnalysisMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "lexicalAnalysis");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow lexicalAnalysisChannel() {
        return f -> f.handle("lexicalAnalysis", "analysis").handle(lexicalAnalysisMessageToApplication(), "transform").channel("aggregateLexicalAnalysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateLexicalAnalysisChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='lexicalAnalysis']").handle(applicationToMessage(), "transform").channel("syntaxAnalysisChannel.input");
    }

    @Bean(name = "syntaxAnalysis")
    SyntaxAnalysisImpl syntaxAnalysis() {
        return new SyntaxAnalysisImpl();
    }

    @Bean
    MessageToApplication syntaxAnalysisMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "syntaxAnalysis");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow syntaxAnalysisChannel() {
        return f -> f.handle("syntaxAnalysis", "analysis").handle(syntaxAnalysisMessageToApplication(), "transform").channel("aggregateSyntaxAnalysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateSyntaxAnalysisChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='syntaxAnalysis']").handle(applicationToMessage(), "transform").channel("semanticAnalysisChannel.input");
    }

    @Bean(name = "semanticAnalysis")
    SemanticAnalysisImpl semanticAnalysis() {
        return new SemanticAnalysisImpl();
    }

    @Bean
    MessageToApplication semanticAnalysisMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "semanticAnalysis");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow semanticAnalysisChannel() {
        return f -> f.handle("semanticAnalysis", "analysis").handle(semanticAnalysisMessageToApplication(), "transform").channel("aggregateSemanticAnalysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateSemanticAnalysisChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='semanticAnalysis']").handle(applicationToMessage(), "transform").channel("postprocessingChannel.input");
    }

    @Bean(name = "postprocessing")
    PostprocessingImpl postprocessing() {
        return new PostprocessingImpl();
    }

    @Bean
    MessageToApplication postprocessingMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "postprocessing");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow postprocessingChannel() {
        return f -> f.handle("postprocessing", "process").handle(postprocessingMessageToApplication(), "transform").channel("aggregatePostprocessingChannel.input");
    }

    @Bean
    public IntegrationFlow aggregatePostprocessingChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='postprocessing']").handle(applicationToMessage(), "transform").channel("linkEditorChannel.input");
    }

    @Bean(name = "linkEditor")
    LinkEditorImpl linkEditor() {
        return new LinkEditorImpl();
    }

    @Bean
    MessageToApplication linkEditorMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "linkEditor");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow linkEditorChannel() {
        return f -> f.handle("linkEditor", "link").handle(linkEditorMessageToApplication(), "transform").channel("aggregateLinkEditorChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateLinkEditorChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='linkEditor']").handle(applicationToMessage(), "transform").channel("outputGenerationChannel.input");
    }

    @Bean(name = "outputGeneration")
    OutputGenerationImpl outputGeneration() {
        return new OutputGenerationImpl();
    }

    @Bean
    MessageToApplication outputGenerationMessageToApplication() {
        return new MessageToApplication(Application.State.TERMINATED, "outputGeneration");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow outputGenerationChannel() {
        return f -> f.handle("outputGeneration", "generation").handle(outputGenerationMessageToApplication(), "transform").channel("aggregateOutputGenerationChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateOutputGenerationChannel() {
        return;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(OrchaCompilerApplication.class).web(WebApplicationType.NONE).run(args);
    }
}
