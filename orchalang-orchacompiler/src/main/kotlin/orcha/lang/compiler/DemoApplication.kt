package orcha.lang.compiler

import orcha.lang.compiler.OrchaCompiler
import orcha.lang.compiler.syntax.ReceiveInstruction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import kotlin.system.exitProcess

@SpringBootApplication(scanBasePackages = ["orcha", "orcha.lang.compiler"])
class DemoApplication{

	@Bean
	fun init(orchaCompiler: OrchaCompiler) = CommandLineRunner {
		if(it.size != 1){
			println("Orcha program is required as argument")
			exitProcess(-1)
		}
		orchaCompiler . compile(it[0])
	}

}

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}


