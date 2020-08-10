package orcha.school

import orcha.lang.configuration.*
import orcha.lang.configuration.EventHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EnrollStudentConfiguration {

    @Bean
    fun studentDatabase(): EventHandler {
        val eventHandler = EventHandler("studentDatabase")
        val databaseAdapter = DatabaseAdapter(connection = DatabaseConnection(entityScanPackage = "orcha.school"))
        eventHandler.input= Input(databaseAdapter,"orcha.school.StudentDomain")
        eventHandler.output = Output(databaseAdapter, "orcha.school.StudentDomain")
        return eventHandler
    }
    @Bean
    fun enrollStudent(): Application {
        val application = Application("enrollStudent", "Kotlin")
        val javaAdapter = JavaServiceAdapter("orcha.school.EnrollStudent", "enroll")
        application.input = Input(javaAdapter, "orcha.school.StudentDomain")
        application.output = Output(javaAdapter, "orcha.school.StudentDomain")
        return application
    }

    /*@Bean
    fun studentOutputDatabase(): EventHandler {
        val eventHandler = EventHandler("studentOutputDatabase")
        val databaseAdapter = DatabaseAdapter(connection = DatabaseConnection(entityScanPackage = "orcha.lang.compiler.school"))
        eventHandler.output = Output(databaseAdapter, "orcha.lang.compiler.school.StudentDomain")
        return eventHandler
    }*/
}