package school

import orcha.lang.configuration.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EnrollStudentConfiguration {

    @Bean
    fun studentDatabase(): EventHandler {
        val eventHandler = EventHandler("studentDatabase")
        val databaseAdapter = DatabaseAdapter(connection = DatabaseConnection(entityScanPackage = "school"))
        eventHandler.input= Input(databaseAdapter,"school.StudentDomain")
        return eventHandler
    }
    @Bean
    fun enrollStudent(): Application {
        val application = Application("enrollStudent", "Kotlin")
        val javaAdapter = JavaServiceAdapter("school.EnrollStudent", "enroll")
        application.input = Input(javaAdapter, "school.StudentDomain")
        application.output = Output(javaAdapter, "school.StudentDomain")
        return application
    }

    @Bean
    fun studentOutputDatabase(): EventHandler {
        val eventHandler = EventHandler("studentOutputDatabase")
        val databaseAdapter = DatabaseAdapter(connection = DatabaseConnection(entityScanPackage = "school"))
        eventHandler.output = Output(databaseAdapter, "school.StudentDomain")
        return eventHandler
    }
}