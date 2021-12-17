package orcha.lang.compiler;

import orcha.lang.compiler.referenceimpl.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class SpringIntegrationAutoConfiguration {

    @ConditionalOnMissingBean
    //@Bean(name="preprocessingForOrchaCompiler")
    @Bean
    public Preprocessing preprocessing() {
        return new PreprocessingImpl();
    }

    @ConditionalOnMissingBean
    //@Bean(name="syntaxAnalysisForOrchaCompiler")
    @Bean
    public SyntaxAnalysis syntaxAnalysis() {
        return new SyntaxAnalysisImpl();
    }

    @ConditionalOnMissingBean
    //@Bean(name="semanticAnalysisForOrchaCompiler")
    @Bean
    public SemanticAnalysis semanticAnalysis()  {
        return new SemanticAnalysisImpl();
    }

    @ConditionalOnMissingBean
    //@Bean(name="postprocessingForOrchaCompiler")
    @Bean
    public Postprocessing postprocessing() {
        return new PostprocessingImpl();
    }

    @ConditionalOnMissingBean
    //@Bean(name="lexicalAnalysisForOrchaCompiler")
    @DependsOn({"whenInstruction", "sendInstruction"})
    public LexicalAnalysis lexicalAnalysis() {
        return new LexicalAnalysisImpl();
    }

    @ConditionalOnMissingBean
    @Bean
    public LinkEditor linkEditor() { return new LinkEditorImpl(); }

    @ConditionalOnMissingBean
    @DependsOn({"outputCodeGenerator"})
    public OutputGeneration outputGeneration() { return new OutputGenerationImpl(); }

    @ConditionalOnMissingBean
    @DependsOn({"outputCodeGenerator"})
    public OutputExportation outputExportation() {
        return new OutputExportationImpl();
    }


}
