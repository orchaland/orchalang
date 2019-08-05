package orcha.lang.compiler.referenceimpl.springIntegration

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

class LinkEditorImpl : LinkEditor {

    @Autowired
    internal var springApplicationContext: ApplicationContext? = null

    @Throws(OrchaCompilationException::class)
    override fun link(orchaProgram: OrchaProgram): OrchaProgram {

        log.info("Link edition of the orcha program \"" + orchaProgram.orchaMetadata.title + "\" begins. ")

        for (node in orchaProgram.integrationGraph) {

            log.info("Link edition  for the node: " + node)
            log.info("Link edition for the instruction: " + node.instruction!!.instruction)

            when (node.integrationPattern) {
                IntegrationNode.IntegrationPattern.SERVICE_ACTIVATOR -> {
                    when(node.instruction){
                        is ComputeInstruction -> {
                            val compute: ComputeInstruction = node.instruction as ComputeInstruction
                            val configuration = springApplicationContext!!.getBean(compute.application)
                            val application: Application = configuration as Application

                        }
                    }
                }
            }
        }

        log.info("Linf edition of the orcha program \"" + orchaProgram.orchaMetadata.title + "\" complete successfuly.")
        return orchaProgram
    }

    companion object {

        private val log = LoggerFactory.getLogger(LinkEditorImpl::class.java)
    }
}
