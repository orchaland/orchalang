package orcha.lang.configuration

import java.util.*

/**
 * @property language the language name like Java.
 */
data class Application(val name: String, val language: String?, var input: Input? = null, var output: Output? = null, var state: State? = null) {

    /**
     * State of a service.<br></br>
     * When the Orcha programmer write expressions like:<br></br>
     * when "application terminates" or when "application fails"<br></br>
     * contains the state of the service.
     * @Since 0.1
     * @author Ben C.
     */

    internal var specifications: String? = null
    internal var description: String? = null

    /**
     * The input event of a service called by a compute instruction, there is a single event but it can contains many messages.
     */
    //var input: Input? = null

    /**
     * The output event of a service called by a compute instruction, there is a single event but it can contains many messages.
     */
    //var output: Output? = null

    /**
     * App state: should not be set programmatically (managed at runtime).
     */
    //var state: State? = null

    internal var error: Error? = null

    enum class State {
        TERMINATED,
        FAILED,
        RUNNING
    }
}