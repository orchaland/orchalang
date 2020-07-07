package orcha.lang.compiler.referenceimpl.springIntegration

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
import orcha.lang.configuration.JavaServiceAdapter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class OutputGenerationImpl : OutputGeneration {

    val outputCodeGenerator: OutputCodeGenerator = OutputCodeGeneratorImpl()
    @Qualifier("preprocessing")
    @Autowired
    private lateinit var preprocessing: Application

    @Qualifier("syntaxAnalysis")
    @Autowired
    private lateinit var syntaxAnalysis: Application
    @Qualifier("semanticAnalysis")
    @Autowired
    private lateinit var semanticAnalysis: Application
    @Qualifier("postprocessing")
    @Autowired
    private lateinit var postprocessing:Application
    @Qualifier("lexicalAnalysis")
    @Autowired
    private lateinit var lexicalAnalysis: Application

    override fun generation(orchaProgram: OrchaProgram) {

        val orchaMetadata = orchaProgram.orchaMetadata

        log.info("Output generation for the orcha program \"" + orchaMetadata.title + "\" begins.")

        val graphOfInstructions = orchaProgram.integrationGraph

        this.export(orchaMetadata, graphOfInstructions)

        log.info("Output generation for the orcha program \"" + orchaMetadata.title + "\" complete successfully.")

    }

    private fun export(orchaMetadata: OrchaMetadata, graphOfInstructions: List<IntegrationNode>) {

        for (node in graphOfInstructions) {

            log.info("Output generation for the node: " + node)
            log.info("Output generation for the instruction: " + node.instruction!!.instruction)

            when(node.integrationPattern) {
                IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER -> {

                    when(node.instruction){
                        is ReceiveInstruction -> {
                            val receive: ReceiveInstruction = node.instruction as ReceiveInstruction
                            val eventHandler = receive.configuration as? EventHandler
                            val adapter = eventHandler?.input?.adapter
                            if (adapter != null) {
                                outputCodeGenerator.inputAdapter(adapter)
                            }
                        }
                        is SendInstruction -> {
                            val send: SendInstruction = node.instruction as SendInstruction
                            val eventHandler = send.configuration as? EventHandler
                            val adapter = eventHandler?.output?.adapter
                            if (adapter != null) {
                                outputCodeGenerator.outputAdapter(adapter)
                            }
                        }
                    }
                }
                IntegrationNode.IntegrationPattern.MESSAGE_FILTER -> {
                    when(node.instruction){
                        is ReceiveInstruction -> {
                            val receive: ReceiveInstruction = node.instruction as ReceiveInstruction
                            val condition = receive.condition
                            outputCodeGenerator.filter(condition)
                        }
                    }
                }
                IntegrationNode.IntegrationPattern.SERVICE_ACTIVATOR -> {
                    when(node.instruction){
                        is ComputeInstruction -> {
                            val compute: ComputeInstruction = node.instruction as ComputeInstruction
                            val application: Application = compute.configuration as Application
                            val adapter: JavaServiceAdapter = application.input?.adapter as JavaServiceAdapter
                            outputCodeGenerator.serviceActivator(adapter,preprocessing,syntaxAnalysis,semanticAnalysis,postprocessing,lexicalAnalysis)
                        }
                    }
                }
                IntegrationNode.IntegrationPattern.AGGREGATOR -> {
                    when(node.instruction){
                        is WhenInstruction -> {
                            val whenInstruction: WhenInstruction = node.instruction as WhenInstruction
                            val expression = whenInstruction.aggregationExpression
                        }
                    }
                }
                else -> {

                }
            }

            val adjacentNodes = node.nextIntegrationNodes

            this.export(orchaMetadata, adjacentNodes)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputGenerationImpl::class.java)
    }

}