package orcha.lang.compiler.referenceimpl.springIntegration.CodeGenerator
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder


object generateDSLApplication {
    @JvmStatic
    fun main(args: Array<String>) {


        try {
            val codeGeneration = jCodeModel()
            codeGeneration.generate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SpringApplicationBuilder(generateDSLApplication::class.java).web(WebApplicationType.NONE).run(*args)
    }
}