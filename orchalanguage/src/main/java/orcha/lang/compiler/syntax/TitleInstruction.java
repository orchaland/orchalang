package orcha.lang.compiler.syntax;

import orcha.lang.compiler.OrchaCompilationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleInstruction extends Instruction {

    String titleSyntax = "title(\\s*):(\\s*)(?<title>.*?)";
    String title;

    public TitleInstruction(String instruction) {
        super(instruction, "title");
    }

    public TitleInstruction(String instruction, int lineNumber) {
        super(instruction, lineNumber, "title");
    }

    @Override
    public void analysis() throws OrchaCompilationException {

        Pattern pattern = Pattern.compile(titleSyntax);
        Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " ").trim());
        if(result.matches() == false) {
            throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
        }

        title = result.group("title");

    }

    public String getTitle() {
        return title;
    }
}
