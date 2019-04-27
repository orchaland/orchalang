package orcha.lang.compiler;

public class OrchaCompilationException extends Exception{

    int lineNumber = -1;
    String instruction;
    String orchaFile;

    public OrchaCompilationException() {
        super();
    }

    public OrchaCompilationException(String message) {
        super(message);
    }

    public OrchaCompilationException(String message, String orchaFile) {
        super(message);
        this.orchaFile = orchaFile;
    }

    public OrchaCompilationException(String message, int lineNumber, String line) {
        super("Error at line " + lineNumber + " for the instruction (" + line + ") cause by: " + message);
        this.lineNumber = this.lineNumber;
        this.instruction = line;
    }

    public OrchaCompilationException(String message, int lineNumber, String line, String orchaFile) {
        super("Error at line " + lineNumber + " for the instruction (" + line + ") cause by: " + message);
        this.lineNumber = this.lineNumber;
        this.instruction = line;
        this.orchaFile = orchaFile;
    }

}
