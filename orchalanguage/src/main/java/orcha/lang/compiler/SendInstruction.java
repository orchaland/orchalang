package orcha.lang.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendInstruction extends Instruction{

	String sendSyntax = "send (?<data>.*?) to (?<destinations>.*?)";
	String data;	
	List<String> destinations = new ArrayList<String>();
	
    public SendInstruction(String sendInstruction) {
		super(sendInstruction.trim(), "send");
	}

	public SendInstruction(String sendInstruction, int id, int lineNumber) {
        super(sendInstruction.trim(), id, lineNumber, "send");
    }

    @Override
    public void analysis() throws OrchaCompilationException {

		Pattern pattern = Pattern.compile(sendSyntax);
		Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " "));
		if(result.matches() == false) {
			throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
		}

		data = result.group("data");
		
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


	public void setData(String data) {
		this.data = data;
	}

	public void setDestinations(List<String> destinations) {
		this.destinations = destinations;
	}
}
