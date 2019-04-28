package orcha.lang.compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveInstruction extends Instruction{

	String receiveSyntax = "receive (?<event>.*?) from (?<source>\\w+)( condition (?<condition>.*))?";
	String event;
	String source;
	String condition;

	public ReceiveInstruction(){
		super("receive");
	}

    public ReceiveInstruction(String receiveInstruction) {
    	super(receiveInstruction.trim(), "receive");
	}

	public ReceiveInstruction(String receiveInstruction, int lineNumber) {
        super(receiveInstruction.trim(), lineNumber, "receive");
    }

    @Override
    public void analysis() throws OrchaCompilationException {

		Pattern pattern = Pattern.compile(receiveSyntax);
		Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " "));
		if(result.matches() == false) {
			throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
		}

		event = result.group("event");
		source = result.group("source");
		condition = result.group("condition");

    }

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
