package configuration.transport

import orcha.lang.configuration.*
import org.springframework.context.annotation.Bean
import service.transport.ProfileUser

class ProductOrderConfiguration {

    @Bean
    fun user(): EventHandler {
        val eventHandler = EventHandler("user")
        val inputFileAdapter = InputFileAdapter("data/input", "request.json")
        eventHandler.input = Input("application/json", "service.transport.Request", inputFileAdapter)
        val outputFileAdapter = OutputFileAdapter("data/output", true, "securityAlert.json", true, OutputFileAdapter.WritingMode.REPLACE)
        eventHandler.output = Output("application/json", "service.airport.SecurityAlert", outputFileAdapter)
        return eventHandler
    }

    @Bean
    fun profileUser(): ProfileUser {
        return ProfileUser()
    }

    @Bean
    fun authenticateUsersRequest(): Application {
        val application = Application("authenticateUsersRequest", "Java")
        val javaAdapter = JavaServiceAdapter("service.transport.ProfileUser", "check")
        application.input = Input("service.transport.Request", javaAdapter)
        application.output = Output("service.transport.Request", javaAdapter)
        return application
    }

}
