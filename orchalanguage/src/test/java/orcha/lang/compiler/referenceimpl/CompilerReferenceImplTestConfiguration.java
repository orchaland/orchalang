package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionFactory;
import orcha.lang.compiler.syntax.WhenInstruction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@SpringBootConfiguration
@Configuration
public class CompilerReferenceImplTestConfiguration {

    @Bean(name = "whenInstruction")
    public WhenInstructionFactory whenInstructionFactory() {
        WhenInstructionFactory factory = new WhenInstructionFactory();
        return factory;
    }

    @Bean
    public WhenInstruction whenInstruction() throws Exception {
        return whenInstructionFactory().getObject();
    }

    @Value("${orcha.pathToBinaryCode:build/resources/main}")
    String pathToBinaryCode;

    @Bean
    Preprocessing preprocessingForTest() {
        return new PreprocessingImpl();
    }

    @Bean
    @DependsOn({"whenInstruction"})
    LexicalAnalysis lexicalAnalysisForTest() {
        return new LexicalAnalysisImpl();
    }

    @Bean
    SyntaxAnalysis syntaxAnalysisForTest() {
        return new SyntaxAnalysisImpl();
    }

    @Bean
    SemanticAnalysis semanticAnalysisForTest() {
        return new SemanticAnalysisImpl();
    }

    @Bean
    Transpiler transpiler() {
        return new TranspileToSpringIntegration(pathToBinaryCode);
    }

}
