package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.compiler.OrchaCompilationException
import orcha.lang.compiler.syntax.WhenInstruction

class WhenInstructionForSpringIntegration : WhenInstruction {

    constructor() : super() {}

    constructor(whenInstruction: String) : super(whenInstruction) {}

    constructor(whenInstruction: String, lineNumber: Int) : super(whenInstruction, lineNumber) {}

    override fun convert(application: WhenInstruction.ApplicationOrEventInExpression): String {
        return if (application.state == WhenInstruction.State.TERMINATES) {
            "([" + (application.order - 1) + "].payload instanceof Transpiler(orcha.lang.App) AND [" + (application.order - 1) + "].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)"
        } else if (application.state == WhenInstruction.State.RECEIVES) {
            "([" + (application.order - 1) + "].payload instanceof Transpiler(orcha.lang.EventHandler))"
        } else {
            "([" + (application.order - 1) + "].payload instanceof Transpiler(orcha.lang.App) AND [" + (application.order - 1) + "].payload.state==Transpiler(orcha.lang.configuration.State).FAILED)"
        }
    }

    @Throws(OrchaCompilationException::class)
    override fun logicalOperator(operator: String): String {
        return if (operator.toUpperCase() == "OR") {
            "OR"
        } else if (operator.toUpperCase() == "AND") {
            "AND"
        } else {
            throw OrchaCompilationException("Forbiden operator: $operator. Only AND or OR are allowed.")
        }
    }

}
