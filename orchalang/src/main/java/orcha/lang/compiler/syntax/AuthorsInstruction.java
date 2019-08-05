package orcha.lang.compiler.syntax;

import orcha.lang.compiler.OrchaCompilationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthorsInstruction extends Instruction {

    String authorsSyntax = "authors(\\s*):(\\s*)(?<authors>.*?)";
    List<String> authors = new ArrayList<String>();

    public AuthorsInstruction(String instruction) {
        super(instruction, "authors");
    }

    public AuthorsInstruction(String instruction, int lineNumber) {
        super(instruction, lineNumber, "authors");
    }

    @Override
    public void analysis() throws OrchaCompilationException {

        Pattern pattern = Pattern.compile(authorsSyntax);
        Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " ").trim());
        if(result.matches() == false) {
            throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
        }

        String params = result.group("authors");

        if(params != null) {
            String[] array = params.split(",");
            for(String s: array) {
                authors.add(s.trim());
            }
        }

    }

    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public String toString() {
        return "AuthorsInstruction{" +
                "authors=" + authors +
                "} " + super.toString();
    }
}
