package orcha.lang.compiler.referenceimpl.springIntegration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "orchalang")
class Properties {
    lateinit var pathToIntegrationGraph: String
}