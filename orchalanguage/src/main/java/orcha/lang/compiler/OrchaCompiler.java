package orcha.lang.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan(basePackages={"orcha.lang.compiler","configuration"})
public class OrchaCompiler implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(OrchaCompiler.class);

    @Autowired
    Preprocessing preprocessing;

    @Autowired
    LexicalAnalysis lexicalAnalysis;

    @Autowired
    SyntaxAnalysis syntaxAnalysis;

    @Autowired
    SemanticAnalysis semanticAnalysis;

    @Override
    public void run(String... args) throws Exception {

        if(args.length != 1){
            throw new OrchaCompilationException("Usage: orchaSourceFile.\nAn orcha source file (.orcha or .groovy extension) should be in ./orcha/source\nPut the file name without the extension (.orcha or .groovy) as the argument of this command.\nIf the orcha file is in a subdirectory of ./orcha/source, add this subdirectory to the command line like directoryName/orchaFileNameWithOutExtension");
        }

        String orchaFileName = args[0];

        List<String> inst = preprocessing.process(orchaFileName);

        OrchaProgram orchaProgram = lexicalAnalysis.analysis(orchaFileName);

        orchaProgram  = syntaxAnalysis.analysis(orchaProgram);

        orchaProgram  = semanticAnalysis.analysis(orchaProgram);

    }

    public static void main(String[] args) {

        log.info("Compilation of the Orcha program begins.");

        SpringApplication application = new SpringApplication(OrchaCompiler.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);

        log.info("Compilation of the Orcha program successful. The Orcha program (orcha.lang.OrchaSpringIntegrationLauncher.groovy) can be launched.");

    }

}
