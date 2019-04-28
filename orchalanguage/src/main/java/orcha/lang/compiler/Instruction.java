package orcha.lang.compiler;

public abstract class Instruction {

	protected String instruction;			// the line of code
	int id;
	int lineNumber;				// the line number into the Orcha source file

	String command;

	public Instruction(String instruction, int id, int lineNumber, String command) {
		this.instruction = instruction;
		this.id = id;
		this.lineNumber = lineNumber;
		this.command = command.toLowerCase();
	}
	
	public Instruction(String instruction, String command) {
		this.instruction = instruction;
		this.command = command.toLowerCase();
	}

	public Instruction(String command){
		this.command = command.toLowerCase();
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

	public String getCommand() {
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
