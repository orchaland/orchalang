package orcha.lang.compiler.referenceimpl.springIntegration

import com.fasterxml.jackson.databind.ObjectMapper
import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.LinkEditor
import orcha.lang.compiler.OrchaCompilationException
import orcha.lang.compiler.OrchaProgram
import orcha.lang.compiler.syntax.ComputeInstruction
import orcha.lang.compiler.syntax.ReceiveInstruction
import orcha.lang.compiler.syntax.SendInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.Application
import orcha.lang.configuration.EventHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import java.io.File

class LinkEditorImpl : LinkEditor {

    @Autowired
    internal var properties: Properties? = null

    @Autowired
    internal var springApplicationContext: ApplicationContext? = null

    @Throws(OrchaCompilationException::class)
    override fun link(orchaProgram: OrchaProgram): OrchaProgram {

        log.info("Link edition of the orcha program \"" + orchaProgram.orchaMetadata.title + "\" begins. ")

        for (node in orchaProgram.integrationGraph) {

            log.info("""Link edition for the node: """ + node)
            log.info("Link edition for the instruction: " + node.instruction!!.instruction)

            when (node.integrationPattern) {

                IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER -> {

                    when(node.instruction){
                        is ReceiveInstruction -> {
                            val receive: ReceiveInstruction = node.instruction as ReceiveInstruction
                            val configuration = springApplicationContext!!.getBean(receive.source)
                            val eventHandler: EventHandler = configuration as EventHandler
                            receive.configuration = eventHandler
                        }
                        is SendInstruction -> {
                            val send: SendInstruction = node.instruction as SendInstruction
                            val map:MutableMap<String,EventHandler> = mutableMapOf()
                            for(destination in send.destinations){
                                val configuration = springApplicationContext!!.getBean(destination)
                                val eventHandler: EventHandler = configuration as EventHandler
                                map.put(destination, eventHandler)
                            }
                            send.configuration = map
                        }
                    }
                }

                IntegrationNode.IntegrationPattern.SERVICE_ACTIVATOR -> {
                    when(node.instruction){
                        is ComputeInstruction -> {
                            val compute: ComputeInstruction = node.instruction as ComputeInstruction
                            val configuration = springApplicationContext!!.getBean(compute.application)
                            val application: Application = configuration as Application
                            compute.configuration = application
                        }
                    }
                }

            }


        }

        val orchaMetadata = orchaProgram.orchaMetadata

        val pathToResources = properties!!.pathToIntegrationGraph
        val file = File(pathToResources)

        val graphOfInstructions = orchaProgram.integrationGraph

        val objectMapper = ObjectMapper()
        val jsonInString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(graphOfInstructions);
        file.writeText(jsonInString)

        log.info("Json configuration file for Spring Integration has been generated: " + file.canonicalPath)

        log.info("Linf edition of the orcha program \"" + orchaProgram.orchaMetadata.title + "\" complete successfuly.")
        return orchaProgram
    }

    companion object {

        private val log = LoggerFactory.getLogger(LinkEditorImpl::class.java)
    }
}
