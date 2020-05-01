package orcha.lang.compiler.referenceimpl.springIntegration

import com.fasterxml.jackson.databind.ObjectMapper
import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.LinkEditor
import orcha.lang.compiler.OrchaCompilationException
import orcha.lang.compiler.OrchaProgram
import orcha.lang.compiler.syntax.ComputeInstruction
import orcha.lang.configuration.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import java.io.File

class LinkEditorImpl : LinkEditor {

    @Autowired
    internal var springApplicationContext: ApplicationContext? = null

    @Throws(OrchaCompilationException::class)
    override fun link(orchaProgram: OrchaProgram): OrchaProgram {

        log.info("Link edition of the orcha program \"" + orchaProgram.orchaMetadata.title + "\" begins. ")

        for (node in orchaProgram.integrationGraph) {

            log.info("Link edition for the node: " + node)
            log.info("Link edition for the instruction: " + node.instruction!!.instruction)

            when (node.integrationPattern) {
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

        val pathToResources = "." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + orchaMetadata.title + ".json"
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
