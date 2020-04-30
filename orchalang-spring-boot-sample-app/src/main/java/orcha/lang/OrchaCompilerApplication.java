package orcha.lang;

import orcha.lang.compiler.OrchaCompiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrchaCompilerApplication implements CommandLineRunner {

    @Autowired
    OrchaCompiler orchaCompiler;

    @Override
    public void run(String... args) throws Exception{
        orchaCompiler.compile("orchaCompiler.orcha");
    }

    public static void main(String[] args) {
        SpringApplication.run(OrchaCompilerApplication.class, args);
    }
}
