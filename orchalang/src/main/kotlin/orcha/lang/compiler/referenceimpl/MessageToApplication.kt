package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.referenceimpl.PostprocessingImpl
import orcha.lang.configuration.Application
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired

class MessageToApplication(val state: Application.State, val applicationName: String) {

    private val log = LoggerFactory.getLogger(MessageToApplication::class.java)

    @Autowired
    lateinit var beans: BeanFactory


    fun transform(message: Any): Application {
        log.info("received message: " + message)
        val application: Application = beans.getBean((applicationName)) as Application
        log.info("application associated with " + applicationName + ": " + application)
        val clonedApplication = application?.copy()
        if (clonedApplication != null) {
            clonedApplication.output!!.value = message
        }
        if (clonedApplication != null) {
            clonedApplication.state = state
        }
        log.info("message and state set to application: " + clonedApplication)
        return clonedApplication
    }
}