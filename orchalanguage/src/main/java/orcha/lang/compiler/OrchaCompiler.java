package orcha.lang.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

public class OrchaCompiler {

    private static Logger log = LoggerFactory.getLogger(OrchaCompiler.class);

    @Autowired
    Preprocessing preprocessing;

    @Autowired
    @Qualifier("lexicalAnalysisForOrchaCompiler")
    LexicalAnalysis lexicalAnalysis;

    @Autowired
    SyntaxAnalysis syntaxAnalysis;

    @Autowired
    SemanticAnalysis semanticAnalysis;

    @Autowired
    Postprocessing postprocessing;

    @Autowired
    OutputGeneration outputGeneration;

    public void compile(String orchaFileName) throws OrchaCompilationException {

        List<String> linesOfCode = preprocessing.process(orchaFileName);

        OrchaProgram orchaProgram = lexicalAnalysis.analysis(linesOfCode);

        orchaProgram  = syntaxAnalysis.analysis(orchaProgram);

        orchaProgram  = postprocessing.process(orchaProgram);

        Object output  = outputGeneration.generation(orchaProgram);

    }

}
