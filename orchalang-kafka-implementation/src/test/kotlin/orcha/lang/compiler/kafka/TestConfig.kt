package orcha.lang.compiler.kafka

import orcha.lang.compiler.LinkEditor
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.OutputGeneration
import orcha.lang.compiler.SemanticAnalysis
import orcha.lang.compiler.referenceimpl.LinkEditorImpl
import orcha.lang.compiler.referenceimpl.OutputCodeGenerator
import orcha.lang.compiler.referenceimpl.OutputGenerationImpl
import orcha.lang.compiler.referenceimpl.SemanticAnalysisImpl
import orcha.lang.compiler.syntax.DomainInstruction
import orcha.lang.compiler.syntax.Instruction
import orcha.lang.configuration.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@SpringBootConfiguration
@Configuration
class TestConfig {

    @Autowired
    private lateinit var properties: Properties

    @Value("\${orcha.pathToBinaryCode:build/resources/main}")
    var pathToBinaryCode: String? = null

    @Bean
    fun semanticAnalysisForKotlinTest(): SemanticAnalysis? {
        return SemanticAnalysisImpl()
    }

    @Bean
    fun linkEditorForTest(): LinkEditor? {
        return LinkEditorImpl()
    }

    @Bean
    fun outputGenerationForTest() : OutputGeneration? {
        return OutputGenerationImpl()
    }

    @Bean(name = ["outputCodeGenerator"])
    fun outputCodeGeneratorFactory(): OutputCodeGeneratorFactory {
        return OutputCodeGeneratorFactory()
    }

    /*@Bean
    @Throws(Exception::class)
    fun outputCodeGenerator(): OutputCodeGenerator? {
        return outputCodeGeneratorFactory().getObject()
    }*/

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

}