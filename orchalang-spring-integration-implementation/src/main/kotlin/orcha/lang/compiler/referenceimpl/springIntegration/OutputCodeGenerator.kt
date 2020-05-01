package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.configuration.ConfigurableProperties
import orcha.lang.configuration.InputFileAdapter
import orcha.lang.configuration.JavaServiceAdapter
import orcha.lang.configuration.OutputFileAdapter

interface OutputCodeGenerator {

    fun inputAdapter(adapter: ConfigurableProperties)
    fun outputAdapter(adapter: ConfigurableProperties)
    fun filter(expression: String)
    fun serviceActivator(adapter: ConfigurableProperties)

}