package orcha.lang.compiler

import orcha.lang.compiler.syntax.Instruction

import java.util.ArrayList

data class IntegrationNode(val instruction: Instruction, var integrationPattern: IntegrationPattern? = null, var nextIntegrationNodes: List<IntegrationNode> = ArrayList()) {

    constructor(instruction: Instruction) : this(instruction, null){
    }
    //val instruction: Instruction? = instruction



    enum class IntegrationPattern {
        CHANNEL_ADAPTER,
        MESSAGE_FILTER,
        MESSAGE_TRANSLATOR,
        MESSAGE_ROUTER,
        SERVICE_ACTIVATOR,
        AGGREGATOR,
        RESEQUENCER
    }

    /*override fun toString(): String {
        return "IntegrationNode(integrationPattern=$integrationPattern, instruction=$instruction, nextIntegrationNodes=$nextIntegrationNodes)"
    }*/

}
