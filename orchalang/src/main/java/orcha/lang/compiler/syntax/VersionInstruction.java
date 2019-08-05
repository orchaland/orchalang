package orcha.lang.compiler.syntax;

import orcha.lang.compiler.OrchaCompilationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionInstruction extends Instruction {

    String versionSyntax = "version(\\s*):(\\s*)(?<version>.*?)";
    String version;

    public VersionInstruction(String instruction) {
        super(instruction, "version");
    }

    public VersionInstruction(String instruction, int lineNumber) {
        super(instruction, lineNumber, "version");
    }

    @Override
    public void analysis() throws OrchaCompilationException {

        Pattern pattern = Pattern.compile(versionSyntax);
        Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " ").trim());
        if(result.matches() == false) {
            throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
        }

        version = result.group("version");

    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "VersionInstruction{" +
                "version='" + version + '\'' +
                "} " + super.toString();
    }
}
