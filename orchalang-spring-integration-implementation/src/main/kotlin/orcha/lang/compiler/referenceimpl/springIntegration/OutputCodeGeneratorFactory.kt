package orcha.lang.compiler.referenceimpl.springIntegration

import org.springframework.beans.factory.config.AbstractFactoryBean

class OutputCodeGeneratorFactory : AbstractFactoryBean<OutputCodeGenerationToSpringIntegrationJavaDSL>() {
    init {
        isSingleton = false
    }

    override fun getObjectType(): Class<*>? {
        return OutputCodeGenerationToSpringIntegrationJavaDSL::class.java
    }

    @Throws(Exception::class)
    override fun createInstance(): OutputCodeGenerationToSpringIntegrationJavaDSL {
        return OutputCodeGenerationToSpringIntegrationJavaDSL()
    }
}