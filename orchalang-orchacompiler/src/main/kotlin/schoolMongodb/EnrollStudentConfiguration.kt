package schoolMongodb

import orcha.lang.configuration.*
import org.springframework.context.annotation.Bean

class EnrollStudentConfiguration {

    @Bean
    fun studentDatabase(): EventHandler {
        val eventHandler = EventHandler("studentDatabase")
        val databaseAdapter = DatabaseAdapterMongodb(connection = DatabaseConnectionMongodb(entityScanPackage = "school"))
        eventHandler.input= Input(databaseAdapter,"schoolMongodb.StudentDomain")
        return eventHandler
    }

    @Bean
    fun enrollStudent(): Application {
        val application = Application("enrollStudent", "Kotlin")
        val javaAdapter = JavaServiceAdapter("schoolMongodb.EnrollStudent", "enroll")
        application.input = Input(javaAdapter, "schoolMongodb.StudentDomain")
        application.output = Output(javaAdapter, "schoolMongodb.StudentDomain")
        return application
    }
}