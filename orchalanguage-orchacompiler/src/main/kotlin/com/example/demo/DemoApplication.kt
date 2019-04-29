package com.example.demo

import orcha.lang.compiler.OrchaCompiler
import orcha.lang.compiler.syntax.ReceiveInstruction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class DemoApplication{

	@Bean
	fun init(orchaCompiler: OrchaCompiler) = CommandLineRunner {
		orchaCompiler . compile("orchaCompiler.orcha")
	}

}

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}


