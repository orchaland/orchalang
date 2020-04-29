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
    @Bean(name="preprocessingForOrchaCompiler")
    public Preprocessing preprocessing() {
        return new PreprocessingImpl();
    }

    @ConditionalOnMissingBean
    @Bean(name="syntaxAnalysisForOrchaCompiler")
    public SyntaxAnalysis syntaxAnalysis() {
        return new SyntaxAnalysisImpl();
    }

    @ConditionalOnMissingBean
    @Bean(name="semanticAnalysisForOrchaCompiler")
    public SemanticAnalysis semanticAnalysis()  {
        return new SemanticAnalysisImpl();
    }

    @ConditionalOnMissingBean
    @Bean(name="postprocessingForOrchaCompiler")
    public Postprocessing postprocessing() {
        return new PostprocessingImpl();
    }

    @Bean(name="lexicalAnalysisForOrchaCompiler")
    @DependsOn({"whenInstruction", "sendInstruction"})
    public LexicalAnalysis lexicalAnalysis() {
        return new LexicalAnalysisImpl();
    }
}
