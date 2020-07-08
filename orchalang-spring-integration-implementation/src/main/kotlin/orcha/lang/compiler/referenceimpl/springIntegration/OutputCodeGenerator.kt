package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.configuration.*

interface OutputCodeGenerator {

    fun inputAdapter(adapter: ConfigurableProperties)
    fun outputAdapter(adapter: ConfigurableProperties)
    fun filter(expression: String)
    fun serviceActivator(adapter: ConfigurableProperties)
}