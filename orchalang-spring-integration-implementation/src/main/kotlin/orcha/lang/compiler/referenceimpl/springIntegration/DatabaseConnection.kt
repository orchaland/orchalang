package orcha.lang.compiler.referenceimpl.springIntegration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class DatabaseConnection{

    @Value("\${spring-datasource-driverClassName}")
    lateinit var driver: String

    @Value("\${spring-datasource-url}")
    lateinit var url: String

    @Value("\${spring-datasource-username}")
    lateinit var login: String

    @Value("\${spring-datasource-password}")
    lateinit var password: String


}
