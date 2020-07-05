package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.configuration.Application
import org.slf4j.LoggerFactory

class ApplicationToMessage {

    private val log = LoggerFactory.getLogger(ApplicationToMessage::class.java)

    fun transform(application: Application): Any? {
        log.info("Received application: " + application)
        val value = application.output?.value
        log.info("Returned value: " + value)
        return value
    }
}