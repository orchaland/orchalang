package essai;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;

@Configuration
@SpringBootApplication(scanBasePackages = {
    "essai"
})
public class OrchaCompilerApplication {

    @Bean
    public IntegrationFlow orchaProgramSourceFlow() {
        return IntegrationFlows.from(Files.inboundAdapter(new File("./files")).patternFilter("*.orcha"), a -> a.poller(Pollers.fixedDelay(1000))).enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).channel("preprocessingChannel.input").get();
    }

    public static void main(String args) {
        new SpringApplicationBuilder("OrchaCompilerApplication.class").web("WebApplicationType.NONE").run("args");
    }

    @Bean(name = "preprocessingForOrchaCompiler")
    PreprocessingImpl preprocessing() {
        return new PreprocessingImpl();
    }

    @Bean
    MessageToApplication preprocessingMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "preprocessing");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow preprocessingChannel() {
        return f -> f.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).handle("preprocessingForOrchaCompiler", "process").handle(preprocessingMessageToApplication(), "transform").channel("aggregatePreprocessingChannel.input");
    }

    @Bean
    public IntegrationFlow aggregatePreprocessingChannel.input() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationStrategy("headers['messageID']")).transform("payload.?[name=='preprocessing']").handle(applicationToMessage(), "transform").channel("lexicalAnalysisChannel.input");
    }

    @Bean(name = "lexicalAnalysisForOrchaCompiler")
    LexicalAnalysisImpl lexicalAnalysis() {
        return new LexicalAnalysisImpl();
    }

    @Bean
    MessageToApplication lexicalAnalysisMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "lexicalAnalysis");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow lexicalAnalysisChannel() {
        return f -> f.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).handle("lexicalAnalysisForOrchaCompiler", "analysis").handle(lexicalAnalysisMessageToApplication(), "transform").channel("aggregateLexicalAnalysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateLexicalAnalysisChannel.input() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationStrategy("headers['messageID']")).transform("payload.?[name=='preprocessing']").handle(applicationToMessage(), "transform").channel("syntaxAnalysisChannel.input");
    }

    @Bean(name = "syntaxAnalysisForOrchaCompiler")
    SyntaxAnalysisImpl syntaxAnalysis() {
        return new SyntaxAnalysisImpl();
    }

    @Bean
    MessageToApplication syntaxAnalysisMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "syntaxAnalysis");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow syntaxAnalysisChannel() {
        return f -> f.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).handle("syntaxAnalysisForOrchaCompiler", "analysis").handle(syntaxAnalysisMessageToApplication(), "transform").channel("aggregateSyntaxAnalysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateSyntaxAnalysisChannel.input() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationStrategy("headers['messageID']")).transform("payload.?[name=='preprocessing']").handle(applicationToMessage(), "transform").channel("semanticAnalysisChannel.input");
    }

    @Bean(name = "semanticAnalysisForOrchaCompiler")
    SemanticAnalysisImpl semanticAnalysis() {
        return new SemanticAnalysisImpl();
    }

    @Bean
    MessageToApplication semanticAnalysisMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "semanticAnalysis");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow semanticAnalysisChannel() {
        return f -> f.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).handle("semanticAnalysisForOrchaCompiler", "analysis").handle(semanticAnalysisMessageToApplication(), "transform").channel("aggregateSemanticAnalysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateSemanticAnalysisChannel.input() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationStrategy("headers['messageID']")).transform("payload.?[name=='preprocessing']").handle(applicationToMessage(), "transform").channel("postprocessingChannel.input");
    }

    @Bean(name = "postprocessingForOrchaCompiler")
    PostprocessingImpl postprocessing() {
        return new PostprocessingImpl();
    }

    @Bean
    MessageToApplication postprocessingMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "postprocessing");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow postprocessingChannel() {
        return f -> f.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).handle("postprocessingForOrchaCompiler", "process").handle(postprocessingMessageToApplication(), "transform").channel("aggregatePostprocessingChannel.input");
    }

    @Bean
    public IntegrationFlow aggregatePostprocessingChannel.input() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationStrategy("headers['messageID']")).transform("payload.?[name=='preprocessing']").handle(applicationToMessage(), "transform").channel("linkEditorChannel.input");
    }

    @Bean(name = "linkEditorForOrchaCompiler")
    LinkEditorImpl linkEditor() {
        return new LinkEditorImpl();
    }

    @Bean
    MessageToApplication linkEditorMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "linkEditor");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow linkEditorChannel() {
        return f -> f.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).handle("linkEditorForOrchaCompiler", "link").handle(linkEditorMessageToApplication(), "transform").channel("aggregateLinkEditorChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateLinkEditorChannel.input() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationStrategy("headers['messageID']")).transform("payload.?[name=='preprocessing']").handle(applicationToMessage(), "transform").channel("outputGenerationChannel.input");
    }

    @Bean(name = "outputGenerationForOrchaCompiler")
    OutputGenerationImpl outputGeneration() {
        return new OutputGenerationImpl();
    }

    @Bean
    MessageToApplication outputGenerationMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "outputGeneration");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow outputGenerationChannel() {
        return f -> f.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).handle("outputGenerationForOrchaCompiler", "generation").handle(outputGenerationMessageToApplication(), "transform").channel("aggregateOutputGenerationChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateOutputGenerationChannel.input() {
        return;
    }
}
