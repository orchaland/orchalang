package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootConfiguration
@Configuration
public class CompilerReferenceImplTestConfiguration {

    @Value("${orcha.pathToBinaryCode:build/resources/main}")
    String pathToBinaryCode;

    @Bean
    SyntaxAnalysis syntaxAnalysisForTest() {
        return new SyntaxAnalysisImpl();
    }

}
