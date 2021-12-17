package orcha.lang.compiler.kafka

import orcha.lang.compiler.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
class DefaultAutoConfiguration {

    //@Qualifier("preprocessingForOrchaCompiler")
    @Autowired
    private lateinit var preprocessing: Preprocessing

    //@Qualifier("lexicalAnalysisForOrchaCompiler")
    @Autowired
    private lateinit var lexicalAnalysis: LexicalAnalysis

    //@Qualifier("syntaxAnalysisForOrchaCompiler")
    @Autowired
    private lateinit var syntaxAnalysis: SyntaxAnalysis

    //@Qualifier("semanticAnalysisForOrchaCompiler")
    @Autowired
    private lateinit var semanticAnalysis: SemanticAnalysis

    //@Qualifier("postprocessingForOrchaCompiler")
    @Autowired
    private lateinit var postprocessing: Postprocessing

    @ConditionalOnMissingBean
    //@DependsOn("preprocessingForOrchaCompiler", "lexicalAnalysisForOrchaCompiler", "syntaxAnalysisForOrchaCompiler", "semanticAnalysisForOrchaCompiler", "postprocessingForOrchaCompiler")
    @Bean
    fun orchaCompiler(): OrchaCompiler {
        //return OrchaCompiler(preprocessing, lexicalAnalysis, syntaxAnalysis, semanticAnalysis, postprocessing, linkEditor(), outputGeneration(), outputExportation())
        return OrchaCompiler(preprocessing, lexicalAnalysis, syntaxAnalysis, semanticAnalysis, postprocessing, linkEditor, outputGeneration, outputExportation)
    }

    //@ConditionalOnMissingBean
    //@Bean("linkEditorForOrchaCompiler")
    //@Bean
    //internal fun linkEditor(): LinkEditor {
      //  return LinkEditorImpl()
    //}

    @Autowired
    private lateinit var linkEditor: LinkEditor


    /*@ConditionalOnMissingBean
    //@Bean("outputGenerationToSpringIntegrationJavaDSL")
    @Bean
    internal fun outputGenerationToSpringIntegrationJavaDSL(): OutputCodeGenerationToSpringIntegrationJavaDSLImpl {
        return OutputCodeGenerationToSpringIntegrationJavaDSLImpl()
    }*/

    /*@ConditionalOnMissingBean
    //@DependsOn("outputGenerationToSpringIntegrationJavaDSL")
    //@Bean("outputGenerationForOrchaCompiler")
    @Bean
    internal fun outputGeneration(): OutputGeneration {
        return OutputGenerationImpl()
    }*/

    @Autowired
    private lateinit var outputGeneration: OutputGeneration

    /*@ConditionalOnMissingBean
    //@DependsOn("outputGenerationToSpringIntegrationJavaDSL")
    //@Bean("outputExportationForOrchaCompiler")
    @Bean
    internal fun outputExportation(): OutputExportation {
        return OutputExportationImpl()
    }*/

    @Autowired
    private lateinit var outputExportation: OutputExportation

}