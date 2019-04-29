package orcha.lang.configuration;

import java.util.Hashtable;
import java.util.Map;

public class Application {

    enum State{
        TERMINATED,
        FAILED,
        RUNNING
    }

    /**
     * State of a service.<br>
     * When the Orcha programmer write expressions like:<br>
     * when "application terminates" or when "application fails"<br>
     * contains the state of the service.
     * @Since 0.1
     * @author Ben C.
     *
     */

    String specifications;
    String name;
    String description;

    /**
     * Groovy, Java, js...
     */
    String language;

    Map properties = new Hashtable();

    /**
     * The input event of a service called by a compute instruction, there is a single event but it can contains many messages.
     */
    Input input;

    /**
     * The output event of a service called by a compute instruction, there is a single event but it can contains many messages.
     */
    Output output;

    /**
     * Application state: should not be set programmatically (managed at runtime).
     */
    State state;

    Error error;
}