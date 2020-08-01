package orcha.lang.compiler.referenceimpl.springIntegration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class DatabaseConnection{

    @Value("\${database-driver}")
    lateinit var driver: String

    @Value("\${database-url}")
    lateinit var url: String

    @Value("\${database-login}")
    lateinit var login: String

    @Value("\${database-password}")
    lateinit var password: String

    @Bean
    fun databaseConnection() : DatabaseConnection {
        val connection: DatabaseConnection = DatabaseConnection()
        if(connection.driver==null||connection.url==null||connection.login==null||
                connection.password==null)throw Exception("driver, url, login and password should not be null.Consider initialization in a property file.")
        return connection
    }

}
