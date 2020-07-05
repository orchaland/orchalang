package orcha.lang.configuration

/**
 * @property mimeType MIME type like application/json
 * @property type type like java.lang.String or void
 * @property adapter an adapter like [orcha.lang.configuration.JavaServiceAdapter]
 */
data class Output(val adapter: ConfigurableProperties, val type: String = "void", val mimeType: String? = null, var value: Any? = null) {

    /**
     * Content of the output event often in Json format, should not be set programmatically.
     */
    //var value: Any? = null

    internal var autoStartup = true
}



