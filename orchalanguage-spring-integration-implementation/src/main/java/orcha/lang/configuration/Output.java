package orcha.lang.configuration;

public class Output {
    /**
     * MIME type like application/json.
     */
    String mimeType;
    /**
     * java.lang.Integer for instance. Primitive types like int forbidden.
     */
    String type;
    /**
     * Content of the output event often in Json format, should not be set programmatically.
     */
    String value;
    /**
     * Any Orcha adapter like {@link orcha.lang.configuration.JavaServiceAdapter}
     */
    ConfigurableProperties adapter;

    boolean autoStartup = true;
}






