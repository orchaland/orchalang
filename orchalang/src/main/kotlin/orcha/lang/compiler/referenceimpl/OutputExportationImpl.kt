package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.OutputExportation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AbstractFactoryBean
import javax.annotation.Resource

class OutputExportationImpl : OutputExportation {

    @Autowired
    internal var properties: Properties? = null

    @Resource(name = "&outputCodeGenerator")
    var outputCodeGenerator: AbstractFactoryBean<*>? = null

    //@Qualifier("outputGenerationToSpringIntegrationJavaDSL")
    //@Autowired
    //private lateinit var outputCodeGenerationToSpringIntegrationJavaDSL: OutputCodeGenerationToSpringIntegrationJavaDSL

    override fun export(`object`: Any) {
        log.info("Output code exportation")
        val outputCodeGenerator : OutputCodeGenerator = outputCodeGenerator?.getObject() as OutputCodeGenerator
        //val outputCodeGenerator : OutputCodeGenerator = outputCodeGenerator as OutputCodeGenerator
        outputCodeGenerator.export(`object`, properties!!.pathToGeneratedCode)
        //outputCodeGenerationToSpringIntegrationJavaDSL.export(`object`)
    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputExportationImpl::class.java)
    }

}