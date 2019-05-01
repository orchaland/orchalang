package orcha.lang.compiler.referenceimpl.springIntegration;

import orcha.lang.compiler.syntax.SendInstruction;

public class SendInstructionForSpringIntegration extends SendInstruction {

    public SendInstructionForSpringIntegration() {
        super();
    }

    public SendInstructionForSpringIntegration(String sendInstruction) {
        super(sendInstruction);
    }

    public SendInstructionForSpringIntegration(String sendInstruction, int lineNumber) {
        super(sendInstruction, lineNumber);
    }

    public String getVariables() {
        return "payload." + super.getVariables();
    }


}
