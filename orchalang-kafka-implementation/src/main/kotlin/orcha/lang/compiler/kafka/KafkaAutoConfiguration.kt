package orcha.lang.compiler.kafka

import orcha.lang.compiler.referenceimpl.OutputCodeGenerator
import orcha.lang.compiler.syntax.SendInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(WhenInstructionForSpringIntegration::class, SendInstructionForSpringIntegration::class, OutputCodeGenerationToKafka::class)
class KafkaAutoConfiguration {

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

    @Bean(name = ["outputCodeGenerator"])
    fun outputCodeGeneratorFactory(): OutputCodeGeneratorFactory {
        return OutputCodeGeneratorFactory()
    }

    @Bean
    @Throws(Exception::class)
    fun outputCodeGenerator(): OutputCodeGenerator? {
        return outputCodeGeneratorFactory().getObject()
    }

}
