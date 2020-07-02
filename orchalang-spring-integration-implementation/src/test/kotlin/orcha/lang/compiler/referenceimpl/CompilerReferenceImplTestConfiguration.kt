package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.LexicalAnalysis
import orcha.lang.compiler.SemanticAnalysis
import orcha.lang.compiler.SyntaxAnalysis
import orcha.lang.compiler.referenceimpl.springIntegration.SendInstructionFactory
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionFactory
import orcha.lang.compiler.syntax.SendInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@SpringBootConfiguration
@Configuration
class CompilerReferenceImplTestConfiguration {

    @Value("\${orcha.pathToBinaryCode:build/resources/main}")
    internal var pathToBinaryCode: String? = null

    @Bean
    @DependsOn("whenInstruction", "sendInstruction")
    internal fun lexicalAnalysisForTest(): LexicalAnalysis {
        return LexicalAnalysisImpl()
    }

    @Bean(name = ["whenInstruction"])
    fun whenInstructionFactory(): WhenInstructionFactory {
        return WhenInstructionFactory()
    }

    @Bean
    @Throws(Exception::class)
    fun whenInstruction(): WhenInstruction? {
        return whenInstructionFactory().getObject()
    }

    @Bean(name = ["sendInstruction"])
    fun sendInstructionFactory(): SendInstructionFactory {
        return SendInstructionFactory()
    }

    @Bean
    @Throws(Exception::class)
    fun sendInstruction(): SendInstruction? {
        return sendInstructionFactory().getObject()
    }

    @Bean
    internal fun syntaxAnalysisForTest(): SyntaxAnalysis {
        return SyntaxAnalysisImpl()
    }

    @Bean
    internal fun semanticAnalysisForTest(): SemanticAnalysis {
        return SemanticAnalysisImpl()
    }
}
