package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.compiler.Postprocessing
import orcha.lang.compiler.referenceimpl.PostprocessingImpl
import orcha.lang.compiler.syntax.SendInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(WhenInstructionForSpringIntegration::class, SendInstructionForSpringIntegration::class)
class SpringIntegrationAutoConfiguration {

    @Bean(name = ["whenInstruction"])
    fun whenInstructionFactory(): WhenInstructionFactory {
        return WhenInstructionFactory()
    }

    @Bean
    @Throws(Exception::class)
    fun whenInstruction(): WhenInstruction? {
        return whenInstructionFactory().getObject()
    }

    @Bean(name = ["sendInstruction"])
    fun sendInstructionFactory(): SendInstructionFactory {
        return SendInstructionFactory()
    }

    @Bean
    @Throws(Exception::class)
    fun sendInstruction(): SendInstruction? {
        return sendInstructionFactory().getObject()
    }

    @Bean
    internal fun postprocessing(): Postprocessing {
        return PostprocessingImpl()
    }

}
