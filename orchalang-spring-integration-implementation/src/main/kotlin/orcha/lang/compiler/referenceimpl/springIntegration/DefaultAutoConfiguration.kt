package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.compiler.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
class DefaultAutoConfiguration {

    @Qualifier("preprocessingForOrchaCompiler")
    @Autowired
    private lateinit var preprocessing: Preprocessing

    @Qualifier("lexicalAnalysisForOrchaCompiler")
    @Autowired
    private lateinit var lexicalAnalysis: LexicalAnalysis

    @Qualifier("syntaxAnalysisForOrchaCompiler")
    @Autowired
    private lateinit var syntaxAnalysis: SyntaxAnalysis

    @Qualifier("semanticAnalysisForOrchaCompiler")
    @Autowired
    private lateinit var semanticAnalysis: SemanticAnalysis

    @Qualifier("postprocessingForOrchaCompiler")
    @Autowired
    private lateinit var postprocessing: Postprocessing

    @ConditionalOnMissingBean
    @DependsOn("preprocessingForOrchaCompiler", "lexicalAnalysisForOrchaCompiler", "syntaxAnalysisForOrchaCompiler", "semanticAnalysisForOrchaCompiler", "postprocessingForOrchaCompiler")
    @Bean
    fun orchaCompiler(): OrchaCompiler {
        return OrchaCompiler(preprocessing, lexicalAnalysis, syntaxAnalysis, semanticAnalysis, postprocessing, linkEditor(), outputGeneration())
    }

    @ConditionalOnMissingBean
    @Bean("linkEditorForOrchaCompiler")
    internal fun linkEditor(): LinkEditor {
        return LinkEditorImpl()
    }

    @ConditionalOnMissingBean
    @Bean("outputGenerationToSpringIntegrationJavaDSL")
    internal fun outputGenerationToSpringIntegrationJavaDSL(): OutputCodeGenerationToSpringIntegrationJavaDSLImpl {
        return OutputCodeGenerationToSpringIntegrationJavaDSLImpl()
    }

    @ConditionalOnMissingBean
    @Bean("outputGenerationToSpringIntegrationJavaDSLJPA")
    internal fun outputGenerationToSpringIntegrationJavaDSLJPA():OutputCodeGenerationToSpringIntegrationJavaDSLJpaImpl  {
        return OutputCodeGenerationToSpringIntegrationJavaDSLJpaImpl ()
    }

    @ConditionalOnMissingBean
    @DependsOn("outputGenerationToSpringIntegrationJavaDSL")
    @Bean("outputGenerationForOrchaCompiler")
    internal fun outputGeneration(): OutputGeneration {
        return OutputGenerationImpl()
    }
}