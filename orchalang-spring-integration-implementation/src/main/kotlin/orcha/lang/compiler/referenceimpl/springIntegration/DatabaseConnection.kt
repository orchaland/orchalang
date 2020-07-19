package orcha.lang.compiler.referenceimpl.springIntegration

import org.springframework.beans.factory.annotation.Value

class DatabaseConnection{

    @Value("\${database-driver}")
    lateinit var driver: String

    @Value("\${database-url}")
    lateinit var url: String

    @Value("\${database-login}")
    lateinit var login: String

    @Value("\${database-password}")
    lateinit var password: String

}
