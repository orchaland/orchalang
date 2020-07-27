package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.*

interface OutputCodeGenerationToSpringIntegrationJavaDSL {

    fun orchaMetadata(orchaMetadata: OrchaMetadata)
    fun inputAdapter(eventHandler: EventHandler, nextIntegrationNodes: List<IntegrationNode>)
    fun outputAdapter(adapter: ConfigurableProperties)
    fun filter(expression: String)
    fun serviceActivator(application: Application, nextIntegrationNodes: List<IntegrationNode>)
    fun aggregator(instruction : WhenInstruction, nextIntegrationNodes: List<IntegrationNode>)
    fun export(orchaMetadata: OrchaMetadata)

}