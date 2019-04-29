package orcha.lang.compiler.syntax;

import orcha.lang.compiler.OrchaCompilationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainInstruction extends Instruction {

    String domainSyntax = "domain(\\s*):(\\s*)(?<domain>.*?)";
    String domain;

    public DomainInstruction(String instruction) {
        super(instruction, "domain");
    }

    public DomainInstruction(String instruction, int lineNumber) {
        super(instruction, lineNumber, "domain");
    }

    @Override
    public void analysis() throws OrchaCompilationException {

        Pattern pattern = Pattern.compile(domainSyntax);
        Matcher result = pattern.matcher(instruction.replaceAll("\\s\\s+", " ").trim());
        if(result.matches() == false) {
            throw new OrchaCompilationException("Syntax error at instruction: " + instruction);
        }

        domain = result.group("domain");

    }

    public String getDomain() {
        return domain;
    }
}
