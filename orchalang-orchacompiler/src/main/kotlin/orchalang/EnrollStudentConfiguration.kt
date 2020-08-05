package orchalang

import orcha.lang.configuration.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class EnrollStudentConfiguration {

    @Bean
    fun studentDatabase(): EventHandler {
        val eventHandler = EventHandler("studentDatabase")
        val databaseAdapter = DatabaseAdapter()
        eventHandler.input= Input(databaseAdapter,"com.example.jpa.EnrollStudent.StudentDomain")
        return eventHandler
    }
    @Bean
    fun enrollStudent(): Application {
        val application = Application("enrollStudent", "Kotlin")
        val javaAdapter = JavaServiceAdapter("com.example.jpa.EnrollStudent", "enroll")
        application.input = Input(javaAdapter, "com.example.jpa.EnrollStudent.StudentDomain")
        application.output = Output(javaAdapter, "com.example.jpa.EnrollStudent.StudentDomain")
        return application
    }

    @Bean
    fun studentOutputDatabase(): EventHandler {
        val eventHandler = EventHandler("studentOutputDatabase")
        val databaseAdapter = DatabaseAdapter()
        eventHandler.output = Output(databaseAdapter, "com.example.jpa.EnrollStudent.StudentDomain")
        return eventHandler
    }
}