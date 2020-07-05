package orcha.lang.compiler

import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata


data class OrchaProgram(var orchaMetadata: OrchaMetadata? = null, var integrationGraph: List<IntegrationNode>? = null) {

    constructor(): this(null, null) {}

}
