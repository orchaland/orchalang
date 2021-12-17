package orcha.lang.configuration

/**
 * @property javaClass full name of the class like orcha.lang.compiler.referenceimpl.LexicalAnalysisImpl
 */
data class JavaServiceAdapter(val javaClass: String, val method: String, val adapter : ServiceAdapter = ServiceAdapter.JavaApplication) : ConfigurableProperties() {

}
