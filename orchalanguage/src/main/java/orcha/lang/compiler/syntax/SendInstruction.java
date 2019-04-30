package orcha.lang.compiler.syntax;

import orcha.lang.compiler.OrchaCompilationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendInstruction extends Instruction {

	String sendSyntax = "send (?<data>.*?)(\\.(?<variables>.*))? to (?<destinations>.*?)";
	String data;
	List<String> variables = new ArrayList<String>();
	List<String> destinations = new ArrayList<String>();
	
    public SendInstruction(String sendInstruction) {
		super(sendInstruction.trim(), "send");
	}

	public SendInstruction(String sendInstruction, int lineNumber) {
        super(sendInstruction.trim(), lineNumber, "send");
    }

    @Override
    public void analysis() throws OrchaCompilationException {

		Pattern pattern = Pattern.compile(sendSyntax);
		Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " ").trim());
		if(result.matches() == false) {
			throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
		}

		data = result.group("data");

		String vars = result.group("variables");
		System.out.println("vars: " +  vars);
		if(vars != null) {
			String[] array = vars.split("\\.");
			System.out.println(array.length);
			for(String s: array) {
				System.out.println("s: " +  s);
				variables.add(s.trim());
			}
		}

		String dest = result.group("destinations");
		if(dest != null) {
			String[] array = dest.split(",");
			for(String s: array) {
				destinations.add(s.trim());
			}
		}

    }

	public String getData() {
		return data;
	}

	public List<String> getDestinations() {
		return destinations;
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public void setData(String data) {
		this.data = data;
	}

	public void setDestinations(List<String> destinations) {
		this.destinations = destinations;
	}
}
