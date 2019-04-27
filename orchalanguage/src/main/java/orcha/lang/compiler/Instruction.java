package orcha.lang.compiler;

public abstract class Instruction {

	protected String instruction;			// the line of code
	int id;
	int lineNumber;				// the line number into the Orcha source file

	public enum Command{
		RECEIVE,
		COMPUTE,
		WHEN,
		SEND
	}

	Command command;

	public Instruction(String instruction, int id, int lineNumber, Command command) {
		this.instruction = instruction;
		this.id = id;
		this.lineNumber = lineNumber;
		this.command = command;
	}
	
	public Instruction(String instruction, Command command) {
		this.instruction = instruction;
		this.command = command;
	}

	public Instruction(Command command){
		this.command = command;
	}

	public abstract void analysis() throws OrchaCompilationException;

	public String getInstruction() {
		return instruction;
	}

	public int getId() {
		return id;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public Command getCommand() {
		return command;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}
}
