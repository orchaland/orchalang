package orcha.lang.compiler.syntax;

import orcha.lang.compiler.OrchaCompilationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescriptionInstruction extends Instruction {

    String descriptionSyntax = "description(\\s*):(\\s*)(?<description>.*?)";
    String description;

    public DescriptionInstruction(String instruction) {
        super(instruction, "description");
    }

    public DescriptionInstruction(String instruction, int lineNumber) {
        super(instruction, lineNumber, "description");
    }

    @Override
    public void analysis() throws OrchaCompilationException {

        Pattern pattern = Pattern.compile(descriptionSyntax);
        Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " ").trim());
        if(result.matches() == false) {
            throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
        }

        description = result.group("description");

    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DescriptionInstruction{" +
                "description='" + description + '\'' +
                "} " + super.toString();
    }
}
