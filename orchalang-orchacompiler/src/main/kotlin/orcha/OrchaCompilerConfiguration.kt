package orcha

import orcha.lang.configuration.*
import orcha.lang.configuration.EventHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrchaCompilerConfiguration {

    @Bean
    fun orchaProgramSource(): EventHandler {
        val eventHandler = EventHandler("orchaProgramSource")
        val fileAdapter = InputFileAdapter("./files", "*.orcha")
        eventHandler.input = Input(fileAdapter, "java.lang.String")
        return eventHandler;
    }

    @Bean
    fun pre_processing(): Application {
        val application = Application("pre_processing", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.PreprocessingImpl", "process")
        application.input = Input(javaAdapter, "java.lang.String")
        application.output = Output(javaAdapter, "java.util.List<java.lang.String>")
        return application
    }

    @Bean
    fun lexical_analysis(): Application {
        val application = Application("lexical_analysis", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.LexicalAnalysisImpl", "analysis")
        application.input = Input(javaAdapter, "java.util.List<java.lang.String>")
        application.output = Output(javaAdapter, "orcha.lang.compiler.OrchaProgram")
        return application
    }

    @Bean
    fun syntax_analysis(): Application {
        val application = Application("syntax_analysis", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.SyntaxAnalysisImpl", "analysis")
        application.input = Input(javaAdapter,"orcha.lang.compiler.OrchaProgram")
        application.output = Output(javaAdapter, "orcha.lang.compiler.OrchaProgram")
        return application
    }

    @Bean
    fun semantic_analysis(): Application {
        val application = Application("semantic_analysis", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.SemanticAnalysisImpl", "analysis")
        application.input = Input(javaAdapter,"orcha.lang.compiler.OrchaProgram")
        application.output = Output(javaAdapter,"orcha.lang.compiler.OrchaProgram")
        return application
    }

    @Bean
    fun post_processing(): Application {
        val application = Application("post_processing", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.PostprocessingImpl", "process")
        application.input = Input(javaAdapter,"orcha.lang.compiler.OrchaProgram")
        application.output = Output(javaAdapter, "orcha.lang.compiler.OrchaProgram")
        return application
    }

    @Bean
    fun link_editor(): Application {
        val application = Application("link_editor", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.springIntegration.LinkEditorImpl", "link")
        application.input = Input(javaAdapter,"orcha.lang.compiler.OrchaProgram")
        application.output = Output(javaAdapter, "orcha.lang.compiler.OrchaProgram")
        return application
    }

    @Bean
    fun output_generation(): Application {
        val application = Application("output_generation", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.springIntegration.OutputGenerationImpl", "generation")
        application.input = Input(javaAdapter, "orcha.lang.compiler.OrchaProgram")
        return application
    }

    @Bean
    fun output_exportation(): Application {
        val application = Application("output_exportation", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.springIntegration.OutputExportationImpl", "export")
        application.input = Input(javaAdapter, "kotlin.Any")
        return application
    }

    /*@Bean
    fun orchaProgramDestination(): EventHandler {
        val eventHandler = EventHandler("orchaProgramDestination")
        val fileAdapter = OutputFileAdapter("data/output", "orchaCompiler.java")
        eventHandler.output = Output(fileAdapter, "application/java-archive")
        return eventHandler
    }*/
}