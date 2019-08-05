package orcha.lang.configuration

/**
 * @property javaClass full name of the class like orcha.lang.compiler.referenceimpl.LexicalAnalysisImpl
 */
class JavaServiceAdapter(val javaClass: String, val method: String) : ConfigurableProperties() {
    val adapter : ServiceAdapter = ServiceAdapter.JavaApplication
}
