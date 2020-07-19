package orcha.lang.configuration

import orcha.lang.compiler.referenceimpl.springIntegration.DatabaseConnection
import org.springframework.beans.factory.annotation.Autowired

class DatabaseAdapter () : ConfigurableProperties(){

    val adapter: Adapter = Adapter.Database

    @Autowired
    lateinit var connection: DatabaseConnection

}
