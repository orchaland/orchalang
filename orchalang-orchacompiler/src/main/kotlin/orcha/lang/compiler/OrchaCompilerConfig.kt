package orcha.lang.compiler

import orcha.lang.compiler.referenceimpl.LexicalAnalysisImpl
import orcha.lang.compiler.referenceimpl.PreprocessingImpl
import orcha.lang.compiler.referenceimpl.SemanticAnalysisImpl
import orcha.lang.compiler.referenceimpl.SyntaxAnalysisImpl
import orcha.lang.compiler.referenceimpl.springIntegration.LinkEditorImpl
import orcha.lang.compiler.referenceimpl.springIntegration.OutputGenerationToSpringIntegration
import orcha.lang.compiler.referenceimpl.springIntegration.OutputGenerationToSpringIntegrationJavaDSL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@Configuration
class OrchaCompilerConfig{

    @Bean
    fun orchaCompiler(): OrchaCompiler{
        return OrchaCompiler()
    }

    /*@Bean(name = ["whenInstruction"])
    fun whenInstructionFactory(): WhenInstructionFactory {
        return WhenInstructionFactory()
    }

    @Bean
    @Throws(Exception::class)
    fun whenInstruction(): WhenInstruction? {
        return whenInstructionFactory().getObject()
    }*/

    @Value("\${orcha.pathToBinaryCode:build/resources/main}")
    internal var pathToBinaryCode: String? = null

    @Bean
    internal fun preprocessing(): Preprocessing {
        return PreprocessingImpl()
    }

    @Bean
    @DependsOn("whenInstruction", "sendInstruction")
    @Qualifier("lexicalAnalysisForOrchaCompiler")
    internal fun lexicalAnalysis(): LexicalAnalysis {
        return LexicalAnalysisImpl()
    }

    @Bean
    internal fun syntaxAnalysis(): SyntaxAnalysis {
        return SyntaxAnalysisImpl()
    }

    @Bean
    internal fun semanticAnalysis(): SemanticAnalysis {
        return SemanticAnalysisImpl()
    }

    @Bean
    internal fun linkEditor(): LinkEditor {
        return LinkEditorImpl()
    }

    /*@Bean
    internal fun outputGeneration(): OutputGeneration {
        return OutputGenerationToSpringIntegration()
    }*/

    @Bean
    internal fun outputGeneration(): OutputGeneration {
        return OutputGenerationToSpringIntegrationJavaDSL()
    }

}