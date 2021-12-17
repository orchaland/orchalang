package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.OrchaProgram
import orcha.lang.compiler.OutputGeneration
import orcha.lang.compiler.syntax.ComputeInstruction
import orcha.lang.compiler.syntax.ReceiveInstruction
import orcha.lang.compiler.syntax.SendInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.Application
import orcha.lang.configuration.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.AbstractFactoryBean
import javax.annotation.Resource

class OutputGenerationImpl : OutputGeneration {

    @Resource(name = "&outputCodeGenerator")
    var outputCodeGenerator: AbstractFactoryBean<*>? = null

    var codeGenerator : OutputCodeGenerator? = null

    //@Qualifier("outputGenerationToSpringIntegrationJavaDSL")
    //@Autowired
    //private lateinit var outputCodeGenerationToSpringIntegrationJavaDSL: OutputCodeGenerationToSpringIntegrationJavaDSL

    override fun generation(orchaProgram: OrchaProgram): Any? {

        //log.info(outputCodeGenerator?.getObject().toString())

        if(codeGenerator == null){
            codeGenerator = outputCodeGenerator?.getObject() as OutputCodeGenerator
        }

        val orchaMetadata = orchaProgram.orchaMetadata

        if (orchaMetadata != null) {
            log.info("Output generation for the orcha program \"" + orchaMetadata.title + "\" begins.")
        }

        val graphOfInstructions = orchaProgram.integrationGraph

        if (orchaMetadata != null) {
            codeGenerator!!.orchaMetadata(orchaMetadata)
            if (graphOfInstructions != null) {
                this.generate(orchaMetadata, graphOfInstructions)
            }
        }

        /*if (orchaMetadata != null) {
            outputCodeGenerationToSpringIntegrationJavaDSL.export(orchaMetadata)
        }*/

        if (orchaMetadata != null) {
            log.info("Output generation for the orcha program \"" + orchaMetadata.title + "\" complete successfully.")
        }

        return codeGenerator!!.getGeneratedCode()

    }

    private fun generate(orchaMetadata: OrchaMetadata, graphOfInstructions: List<IntegrationNode>) {

        //val codeGenerator : OutputCodeGenerator = outputCodeGenerator?.getObject() as OutputCodeGenerator
        if(codeGenerator == null){
            codeGenerator = outputCodeGenerator?.getObject() as OutputCodeGenerator
        }

        for (node in graphOfInstructions) {

            log.info("Output generation for the node: " + node)
            log.info("Output generation for the instruction: " + node.instruction!!.instruction)

            when(node.integrationPattern) {
                IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER -> {
                    when(node.instruction){
                        is ReceiveInstruction -> {
                            val receive: ReceiveInstruction = node.instruction as ReceiveInstruction
                            val eventHandler = receive.configuration as? EventHandler
                            if (eventHandler != null) {
                                codeGenerator!!.inputAdapter(eventHandler, node.nextIntegrationNodes)
                            }
                        }
                        is SendInstruction -> {
                            val send: SendInstruction = node.instruction as SendInstruction
                            val eventHandlers = send.configuration as? MutableMap<String,EventHandler>
                            if (eventHandlers != null) {
                                codeGenerator!!.outputAdapter(eventHandlers.values.first(), node.nextIntegrationNodes)
                            }
                        }
                    }
                }
                IntegrationNode.IntegrationPattern.MESSAGE_FILTER -> {
                    when(node.instruction){
                        is ReceiveInstruction -> {
                            val receive: ReceiveInstruction = node.instruction as ReceiveInstruction
                            val condition = receive.condition
                            codeGenerator!!.filter(condition)
                        }
                    }
                }
                IntegrationNode.IntegrationPattern.SERVICE_ACTIVATOR -> {
                    when(node.instruction){
                        is ComputeInstruction -> {
                            val compute: ComputeInstruction = node.instruction as ComputeInstruction
                            val application: Application = compute.configuration as Application
                            //val adapter: JavaServiceAdapter = application.input?.adapter as JavaServiceAdapter
                            codeGenerator!!.serviceActivator(application, node.nextIntegrationNodes)
                        }
                    }
                }
                IntegrationNode.IntegrationPattern.AGGREGATOR -> {
                    when(node.instruction){
                        is WhenInstruction -> {
                            val whenInstruction: WhenInstruction = node.instruction as WhenInstruction
                            codeGenerator!!.aggregator(whenInstruction, node.nextIntegrationNodes)
                        }
                    }
                }
                else -> {

                }
            }

            //val adjacentNodes = node.nextIntegrationNodes

            //this.generate(orchaMetadata, adjacentNodes)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputGenerationImpl::class.java)
    }

}