package orcha.lang.compiler.referenceimpl.springIntegration

import com.fasterxml.jackson.databind.ObjectMapper
import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.OrchaProgram
import orcha.lang.compiler.OutputGeneration
import orcha.lang.compiler.syntax.ComputeInstruction
import orcha.lang.compiler.syntax.ReceiveInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.Application
import orcha.lang.configuration.ConfigurableProperties
import org.slf4j.LoggerFactory
import java.io.File
import org.springframework.core.io.support.SpringFactoriesLoader



class OutputGenerationToSpringIntegrationJavaDSL : OutputGeneration {

    override fun generation(orchaProgram: OrchaProgram) {

        val orchaMetadata = orchaProgram.orchaMetadata

        log.info("Generation of the output (Spring Integration Java DSL) for the orcha program \"" + orchaMetadata.title + "\" begins.")

        val graphOfInstructions = orchaProgram.integrationGraph

        this.export(orchaMetadata, graphOfInstructions)

        val pathToResources = "." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + orchaMetadata.title + ".json"
        val file = File(pathToResources)


        val objectMapper = ObjectMapper()
        val jsonInString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(graphOfInstructions);
        file.writeText(jsonInString)

        log.info("XML configuration file for Spring Integration has been generated: " + file.canonicalPath)
        log.info("Generation of the output (Spring Integration Java DSL) for the orcha program \"" + orchaMetadata.title + "\" complete successfully.")
    }

    private fun export(orchaMetadata: OrchaMetadata, graphOfInstructions: List<IntegrationNode>) {

        for (node in graphOfInstructions) {

            log.info("Generation of Java DSL code for the node: " + node)
            log.info("Generation of Java DSL code for the instruction: " + node.instruction!!.instruction)

            when(node.integrationPattern) {
                IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER -> {


                }
                IntegrationNode.IntegrationPattern.MESSAGE_FILTER -> {
                    when(node.instruction){
                        is ReceiveInstruction -> {
                            val receive: ReceiveInstruction = node.instruction as ReceiveInstruction
                        }
                    }
                }
                IntegrationNode.IntegrationPattern.SERVICE_ACTIVATOR -> {
                    when(node.instruction){
                        is ComputeInstruction -> {
                            val compute: ComputeInstruction = node.instruction as ComputeInstruction
                            val application: Application = compute.configuration as Application
                            print("=============" + (application.input?.adapter ?: null))
                            //val foos = SpringFactoriesLoader.loadFactories(compute.configuration.input.adapter.class, null)

                        }
                    }
                }
                IntegrationNode.IntegrationPattern.AGGREGATOR -> {
                    when(node.instruction){
                        is WhenInstruction -> {
                            val whenInstruction: WhenInstruction = node.instruction as WhenInstruction
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
        private val log = LoggerFactory.getLogger(OutputGenerationToSpringIntegrationJavaDSL::class.java)
    }

}
