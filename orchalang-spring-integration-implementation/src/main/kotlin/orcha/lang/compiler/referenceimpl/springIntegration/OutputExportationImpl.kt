package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.compiler.OutputExportation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class OutputExportationImpl : OutputExportation {

    //@Qualifier("outputGenerationToSpringIntegrationJavaDSL")
    @Autowired
    private lateinit var outputCodeGenerationToSpringIntegrationJavaDSL: OutputCodeGenerationToSpringIntegrationJavaDSL

    override fun export(`object`: Any) {
        log.info("Output code exportation")
        outputCodeGenerationToSpringIntegrationJavaDSL.export(`object`)
    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputExportationImpl::class.java)
    }

}