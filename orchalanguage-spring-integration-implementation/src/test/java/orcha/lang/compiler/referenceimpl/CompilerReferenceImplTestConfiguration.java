package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.springIntegration.SpringIntegrationAutoConfiguration;
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

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
