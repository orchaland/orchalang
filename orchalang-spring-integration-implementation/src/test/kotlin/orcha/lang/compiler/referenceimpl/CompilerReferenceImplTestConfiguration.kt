package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.LexicalAnalysis
import orcha.lang.compiler.LinkEditor
import orcha.lang.compiler.SemanticAnalysis
import orcha.lang.compiler.SyntaxAnalysis
import orcha.lang.compiler.referenceimpl.springIntegration.LinkEditorImpl
import orcha.lang.compiler.referenceimpl.springIntegration.SendInstructionFactory
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionFactory
import orcha.lang.compiler.syntax.SendInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn

@SpringBootConfiguration
@TestConfiguration
class CompilerReferenceImplTestConfiguration {

    //@Value("\${orcha.pathToBinaryCode:build/resources/main}")
    //internal var pathToBinaryCode: String? = null

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

    @Bean
    fun travelAgency(): EventHandler {
        val eventHandler = EventHandler("travelAgency")
        val fileAdapter = InputFileAdapter("./files", "*.json")
        eventHandler.input = Input(fileAdapter, "java.lang.String")
        return eventHandler;
    }

    @Bean
    fun checkOrder(): Application {
        val application = Application("checkOrder", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.PreprocessingImpl", "process")
        application.input = Input(javaAdapter, "java.lang.String")
        application.output = Output(javaAdapter, "java.util.List<java.lang.String>")
        return application
    }

    @Bean
    fun customer(): EventHandler {
        val eventHandler = EventHandler("customer")
        val fileAdapter = OutputFileAdapter("data/output", "orchaCompiler.java")
        eventHandler.output = Output(fileAdapter, "application/java-archive")
        return eventHandler
    }

    @Bean
    internal fun linkEditorForTest(): LinkEditor {
        return LinkEditorImpl()
    }
}
