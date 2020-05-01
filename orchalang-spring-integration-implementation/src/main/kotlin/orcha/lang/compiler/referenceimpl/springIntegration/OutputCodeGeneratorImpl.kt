package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.configuration.ConfigurableProperties
import orcha.lang.configuration.InputFileAdapter
import orcha.lang.configuration.JavaServiceAdapter
import orcha.lang.configuration.OutputFileAdapter
import org.slf4j.LoggerFactory

class OutputCodeGeneratorImpl : OutputCodeGenerator {

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

    companion object {
        private val log = LoggerFactory.getLogger(OutputCodeGeneratorImpl::class.java)
    }

}