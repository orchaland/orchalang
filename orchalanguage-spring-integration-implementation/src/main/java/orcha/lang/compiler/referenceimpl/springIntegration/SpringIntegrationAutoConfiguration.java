package orcha.lang.compiler.referenceimpl.springIntegration;

import orcha.lang.compiler.WhenInstruction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(WhenInstructionForSpringIntegration.class)
public class SpringIntegrationAutoConfiguration {

    @Bean(name = "whenInstruction")
    public WhenInstructionFactory whenInstructionFactory() {
        WhenInstructionFactory factory = new WhenInstructionFactory();
        return factory;
    }

    @Bean
    public WhenInstruction whenInstruction() throws Exception {
        return whenInstructionFactory().getObject();
    }
}
