package orcha.lang.configuration

class Input {
    /**
     * MIME type like application/json.
     */
    internal var mimeType: String? = null
    /**
     * java.lang.Integer for instance. Primitive types like int forbidden.
     */
    internal var type: String? = null
    /**
     * Content of the input event often in Json format, should not be set programmatically.
     */
    internal var value: String? = null
    /**
     * Any Orcha adapter like [orcha.lang.configuration.JavaServiceAdapter]
     */
    internal var adapter: Any? = null

    internal var autoStartup = true
}
