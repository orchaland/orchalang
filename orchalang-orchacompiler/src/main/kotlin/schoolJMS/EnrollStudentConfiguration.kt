package schoolJMS

import orcha.lang.configuration.*
import org.springframework.context.annotation.Bean

class EnrollStudentConfiguration {
    @Bean
    fun studentDatabase(): EventHandler {
        val eventHandler = EventHandler("studentDatabase")
        val messagingMiddlewareAdapter = MessagingMiddlewareAdapter()
        eventHandler.input= Input(messagingMiddlewareAdapter,"school.StudentDomain")
        eventHandler.output = Output(messagingMiddlewareAdapter, "school.StudentDomain")
        return eventHandler
    }

    @Bean
    fun enrollStudent(): Application {
        val  application = Application( "enrollStudent","Kotlin")
        val  javaAdapter = JavaServiceAdapter("school.EnrollStudent", "enroll" )
        application.input = Input(javaAdapter, "school.EnrollStudent")
        application.output  = Output(javaAdapter, "school.StudentDomain")
        return application
    }
}