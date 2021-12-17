package orcha.lang.compiler.kafka

import orcha.lang.compiler.syntax.SendInstruction

class SendInstructionForSpringIntegration : SendInstruction {

    constructor() : super() {}

    constructor(sendInstruction: String) : super(sendInstruction) {}

    constructor(sendInstruction: String, lineNumber: Int) : super(sendInstruction, lineNumber) {}

    override fun getVariables(): String {
        return "payload." + super.getVariables()
    }


}
