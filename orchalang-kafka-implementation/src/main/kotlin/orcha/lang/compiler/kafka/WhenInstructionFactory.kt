package orcha.lang.compiler.kafka

import org.springframework.beans.factory.config.AbstractFactoryBean

class WhenInstructionFactory : AbstractFactoryBean<WhenInstructionForSpringIntegration>() {
    init {
        isSingleton = false
    }

    override fun getObjectType(): Class<*>? {
        return WhenInstructionForSpringIntegration::class.java
    }

    @Throws(Exception::class)
    override fun createInstance(): WhenInstructionForSpringIntegration {
        return WhenInstructionForSpringIntegration()
    }
}
