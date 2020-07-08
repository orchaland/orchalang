package orcha.lang.compiler.referenceimpl.springIntegration

import com.helger.jcodemodel.EClassType
import com.helger.jcodemodel.JAnnotationUse
import com.helger.jcodemodel.JCodeModel
import com.helger.jcodemodel.JMod
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.configuration.ConfigurableProperties
import orcha.lang.configuration.InputFileAdapter
import orcha.lang.configuration.JavaServiceAdapter
import orcha.lang.configuration.OutputFileAdapter
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import java.io.File

class OutputCodeGenerationToSpringIntegrationJavaDSLImpl : OutputCodeGenerationToSpringIntegrationJavaDSL {

    val codeModel = JCodeModel()

    override fun orchaMetadata(orchaMetadata: OrchaMetadata) {

        val className = orchaMetadata.domain + "." + orchaMetadata.title + "Application"

        log.info("Generated class name: " + className)

        val generatedClass = codeModel._class(JMod.PUBLIC, className , EClassType.CLASS)

        generatedClass.annotate(Configuration::class.java)
        val jAnnotation: JAnnotationUse = generatedClass.annotate(SpringBootApplication::class.java)
        jAnnotation.paramArray("scanBasePackages", orchaMetadata.domain);
    }

    override fun inputAdapter(adapter: ConfigurableProperties) {
        when(adapter){
            is InputFileAdapter -> {
                val inputFileAdapter: InputFileAdapter = adapter as InputFileAdapter
                log.info("Generation of the output code for " + inputFileAdapter)
            }
        }
    }

    override fun outputAdapter(adapter: ConfigurableProperties) {
        when(adapter){
            is OutputFileAdapter -> {
                val outputFileAdapter: OutputFileAdapter = adapter as OutputFileAdapter
                log.info("Generation of the output code for " + outputFileAdapter)
            }
        }
    }

    override fun filter(expression: String) {
        log.info("Generation of a filter for the expression " + expression)
    }

    override fun serviceActivator(adapter: ConfigurableProperties) {
        when(adapter){
            is JavaServiceAdapter -> {
                val javaServiceAdapter: JavaServiceAdapter = adapter as JavaServiceAdapter
                log.info("Generation of a service activator for " + javaServiceAdapter)
            }
        }
    }

    override fun export() {
        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source" )
        codeModel.build(file)
    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputCodeGenerationToSpringIntegrationJavaDSLImpl::class.java)
    }

}