package orcha.lang.compiler.referenceimpl.springIntegration;

import orcha.lang.compiler.OrchaCompilationException;
import orcha.lang.compiler.WhenInstruction;

public class WhenInstructionForSpringIntegration extends WhenInstruction {

	public WhenInstructionForSpringIntegration() {
		super();
	}

	public WhenInstructionForSpringIntegration(String whenInstruction ) {
		super(whenInstruction);
	}
	
	public WhenInstructionForSpringIntegration(String whenInstruction, int instructionID, int lineNumber) {
		super(whenInstruction, instructionID, lineNumber);
	}
	
	public String convert(ApplicationOrEventInExpression application) {
		if(application.getState() == State.TERMINATES) {
			return "([" + (application.getOrder()-1) + "].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [" + (application.getOrder()-1) + "].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)";
		} else if(application.getState() == State.RECEIVES){
			return "([" + (application.getOrder()-1) + "].payload instanceof Transpiler(orcha.lang.configuration.EventHandler))";
		} else {
			return "([" + (application.getOrder()-1) + "].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [" + (application.getOrder()-1) + "].payload.state==Transpiler(orcha.lang.configuration.State).FAILED)";
		}
	}
	
	public String logicalOperator(String operator) throws OrchaCompilationException {		
		if(operator.toUpperCase().equals("OR")) {
			return "OR";
		} else if(operator.toUpperCase().equals("AND")) {
			return "AND";
		} else {
			throw new OrchaCompilationException("Forbiden operator: " + operator + ". Only AND or OR are allowed.");
		}
	}

}
