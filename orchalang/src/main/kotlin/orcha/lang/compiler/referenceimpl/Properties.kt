package orcha.lang.compiler.referenceimpl

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "orchalang")
//@Profile("spring-intregation")
class Properties {
    lateinit var pathToIntegrationGraph: String
    lateinit var pathToGeneratedCode: String
}