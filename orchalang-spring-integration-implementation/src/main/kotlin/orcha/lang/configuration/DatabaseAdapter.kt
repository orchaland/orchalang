package orcha.lang.configuration

import orcha.lang.compiler.referenceimpl.springIntegration.DatabaseConnection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean

class DatabaseAdapter () : ConfigurableProperties(){

    val adapter: Adapter = Adapter.Database

  // @Autowired
   //lateinit var connection: DatabaseConnection


}
