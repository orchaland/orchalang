package orcha.lang.configuration

/**
 * @property mimeType MIME type like application/json
 * @property type type like java.lang.String or void
 * @property adapter an adapter like [orcha.lang.configuration.JavaServiceAdapter]
 */
class Input(val adapter: ConfigurableProperties, val type: String = "void", val mimeType: String? = null) {

    /**
     * Content of the input event often in Json format, should not be set programmatically.
     */
    internal var value: String? = null

    internal var autoStartup = true
}
