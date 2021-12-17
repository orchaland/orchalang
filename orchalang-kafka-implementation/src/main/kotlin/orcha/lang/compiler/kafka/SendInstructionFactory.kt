package orcha.lang.compiler.kafka

import org.springframework.beans.factory.config.AbstractFactoryBean

class SendInstructionFactory : AbstractFactoryBean<SendInstructionForSpringIntegration>() {
    init {
        isSingleton = false
    }

    override fun getObjectType(): Class<*>? {
        return SendInstructionForSpringIntegration::class.java
    }

    @Throws(Exception::class)
    override fun createInstance(): SendInstructionForSpringIntegration {
        return SendInstructionForSpringIntegration()
    }
}
