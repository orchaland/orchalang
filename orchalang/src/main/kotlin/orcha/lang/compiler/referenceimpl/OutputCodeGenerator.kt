package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.Application
import orcha.lang.configuration.EventHandler

interface OutputCodeGenerator {
    abstract fun orchaMetadata(orchaMetadata: OrchaMetadata)
    abstract fun getGeneratedCode(): Any?
    abstract fun inputAdapter(eventHandler: EventHandler, nextIntegrationNodes: List<IntegrationNode>)
    abstract fun outputAdapter(first: EventHandler, nextIntegrationNodes: List<IntegrationNode>)
    abstract fun filter(condition: String?)
    abstract fun serviceActivator(application: Application, nextIntegrationNodes: List<IntegrationNode>)
    abstract fun aggregator(whenInstruction: WhenInstruction, nextIntegrationNodes: List<IntegrationNode>)
    abstract fun export(generatedCode: Any, pathToGeneratedCode: String)
}