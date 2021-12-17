package orcha.lang.configuration

import orcha.lang.configuration.Input
import orcha.lang.configuration.Output

data class EventHandler(val name: String, var input: Input? = null, var output: Output? = null) {

    //var output: Output? = null
    //var input: Input? = null
    internal var usersRole: String? = null

}
