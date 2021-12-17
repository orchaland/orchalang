package orcha.lang.compiler.kafka

import org.springframework.beans.factory.config.AbstractFactoryBean

class OutputCodeGeneratorFactory : AbstractFactoryBean<OutputCodeGenerationToKafka>() {
    init {
        isSingleton = false
    }

    override fun getObjectType(): Class<*>? {
        return OutputCodeGenerationToKafka::class.java
    }

    @Throws(Exception::class)
    override fun createInstance(): OutputCodeGenerationToKafka {
        return OutputCodeGenerationToKafka()
    }
}