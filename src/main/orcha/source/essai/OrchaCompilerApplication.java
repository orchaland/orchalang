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
import orcha.lang.compiler.referenceimpl.springIntegration.OutputExportationImpl;
import orcha.lang.compiler.referenceimpl.springIntegration.OutputGenerationImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;

@SpringBootApplication(scanBasePackages = {
    "essai"
})
public class OrchaCompilerApplication {

    public static void main() {
        new SpringApplicationBuilder(OrchaCompilerApplication.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public IntegrationFlow orchaProgramSourceFlow() {
        return IntegrationFlows.from(Files.inboundAdapter(new File("./files")).patternFilter("*.orcha"), a -> a.poller(Pollers.fixedDelay(1000))).enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()")).channel("pre_processingChannel.input").get();
    }

    @Bean
    PreprocessingImpl preprocessingImpl() {
        return new PreprocessingImpl();
    }

    @Bean
    MessageToApplication pre_processingMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "pre_processing");
    }

    @Bean
    ApplicationToMessage applicationToMessage() {
        return new ApplicationToMessage();
    }

    @Bean
    public IntegrationFlow pre_processingChannel() {
        return f -> f.handle("preprocessingImpl", "process").handle(pre_processingMessageToApplication(), "transform").channel("aggregatePre_processingChannel.input");
    }

    @Bean
    public IntegrationFlow aggregatePre_processingChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='pre_processing']").handle(applicationToMessage(), "transform").channel("lexical_analysisChannel.input");
    }

    @Bean
    LexicalAnalysisImpl lexicalAnalysisImpl() {
        return new LexicalAnalysisImpl();
    }

    @Bean
    MessageToApplication lexical_analysisMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "lexical_analysis");
    }

    @Bean
    public IntegrationFlow lexical_analysisChannel() {
        return f -> f.handle("lexicalAnalysisImpl", "analysis").handle(lexical_analysisMessageToApplication(), "transform").channel("aggregateLexical_analysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateLexical_analysisChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='lexical_analysis']").handle(applicationToMessage(), "transform").channel("syntax_analysisChannel.input");
    }

    @Bean
    SyntaxAnalysisImpl syntaxAnalysisImpl() {
        return new SyntaxAnalysisImpl();
    }

    @Bean
    MessageToApplication syntax_analysisMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "syntax_analysis");
    }

    @Bean
    public IntegrationFlow syntax_analysisChannel() {
        return f -> f.handle("syntaxAnalysisImpl", "analysis").handle(syntax_analysisMessageToApplication(), "transform").channel("aggregateSyntax_analysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateSyntax_analysisChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='syntax_analysis']").handle(applicationToMessage(), "transform").channel("semantic_analysisChannel.input");
    }

    @Bean
    SemanticAnalysisImpl semanticAnalysisImpl() {
        return new SemanticAnalysisImpl();
    }

    @Bean
    MessageToApplication semantic_analysisMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "semantic_analysis");
    }

    @Bean
    public IntegrationFlow semantic_analysisChannel() {
        return f -> f.handle("semanticAnalysisImpl", "analysis").handle(semantic_analysisMessageToApplication(), "transform").channel("aggregateSemantic_analysisChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateSemantic_analysisChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='semantic_analysis']").handle(applicationToMessage(), "transform").channel("post_processingChannel.input");
    }

    @Bean
    PostprocessingImpl postprocessingImpl() {
        return new PostprocessingImpl();
    }

    @Bean
    MessageToApplication post_processingMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "post_processing");
    }

    @Bean
    public IntegrationFlow post_processingChannel() {
        return f -> f.handle("postprocessingImpl", "process").handle(post_processingMessageToApplication(), "transform").channel("aggregatePost_processingChannel.input");
    }

    @Bean
    public IntegrationFlow aggregatePost_processingChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='post_processing']").handle(applicationToMessage(), "transform").channel("link_editorChannel.input");
    }

    @Bean
    LinkEditorImpl linkEditorImpl() {
        return new LinkEditorImpl();
    }

    @Bean
    MessageToApplication link_editorMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "link_editor");
    }

    @Bean
    public IntegrationFlow link_editorChannel() {
        return f -> f.handle("linkEditorImpl", "link").handle(link_editorMessageToApplication(), "transform").channel("aggregateLink_editorChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateLink_editorChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='link_editor']").handle(applicationToMessage(), "transform").channel("output_generationChannel.input");
    }

    @Bean
    OutputGenerationImpl outputGenerationImpl() {
        return new OutputGenerationImpl();
    }

    @Bean
    MessageToApplication output_generationMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "output_generation");
    }

    @Bean
    public IntegrationFlow output_generationChannel() {
        return f -> f.handle("outputGenerationImpl", "generation").handle(output_generationMessageToApplication(), "transform").channel("aggregateOutput_generationChannel.input");
    }

    @Bean
    public IntegrationFlow aggregateOutput_generationChannel() {
        return f -> f.aggregate(a -> a.releaseExpression("size()==1 AND (((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))").correlationStrategy("headers['messageID']")).transform("payload.?[name=='output_generation']").handle(applicationToMessage(), "transform").channel("output_exportationChannel.input");
    }

    @Bean
    OutputExportationImpl outputExportationImpl() {
        return new OutputExportationImpl();
    }

    @Bean
    MessageToApplication output_exportationMessageToApplication() {
        return new MessageToApplication("Application.State.TERMINATED", "output_exportation");
    }

    @Bean
    public IntegrationFlow output_exportationChannel() {
        return f -> f.handle("outputExportationImpl", "export").handle();
    }
}
