package orcha.lang.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComputeInstruction extends Instruction{

	String computeSyntax = "compute (?<application>.*?)( with (?<parameters>.*))?";
	String application;	
	List<String> parameters = new ArrayList<String>();
	
    public ComputeInstruction(String computeInstruction) {
		super(computeInstruction.trim(), Command.COMPUTE);
	}

	public ComputeInstruction(String computeInstruction, int id, int lineNumber) {
        super(computeInstruction.trim(), id, lineNumber, Command.COMPUTE);
    }

    @Override
    public void analysis() throws OrchaCompilationException {

		Pattern pattern = Pattern.compile(computeSyntax);
		Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " "));
		if(result.matches() == false) {
			throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
		}

		application = result.group("application");
		
		String params = result.group("parameters");
		
		if(params != null) {
			String[] array = params.split(",");
			for(String s: array) {
				parameters.add(s.trim());
			}
		}

    }

	public String getApplication() {
		return application;
	}

	public List<String> getParameters() {
		return parameters;
	}

}
