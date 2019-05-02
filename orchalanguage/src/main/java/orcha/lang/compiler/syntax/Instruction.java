package orcha.lang.compiler.syntax;

import orcha.lang.compiler.OrchaCompilationException;

public abstract class Instruction {

	protected String instruction;		// the line of code
	int lineNumber;						// the line number into the Orcha source file

	String command;

	public Instruction(String instruction, int lineNumber, String command) {
		this.instruction = instruction;
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

	public int getLineNumber() {
		return lineNumber;
	}

	public String getCommand() {
		return command;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	@Override
	public String toString() {
		return "Instruction{" +
				"instruction='" + instruction + '\'' +
				", lineNumber=" + lineNumber +
				", command='" + command + '\'' +
				'}';
	}
}
