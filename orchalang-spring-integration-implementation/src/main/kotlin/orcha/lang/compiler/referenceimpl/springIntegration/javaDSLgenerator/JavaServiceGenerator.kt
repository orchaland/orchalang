package orcha.lang.compiler.referenceimpl.springIntegration.javaDSLgenerator

import com.helger.jcodemodel.EClassType
import com.helger.jcodemodel.JCodeModel
import com.helger.jcodemodel.JMethod
import com.helger.jcodemodel.JMod
import org.springframework.context.annotation.Bean
import java.io.File


class JavaServiceGenerator {

    fun generate(javaClass: String, method: String) {
        val codeModel = JCodeModel()
        val className = "com.example.integrationdsl." + "RestWebService"
        val webService = codeModel._class(JMod.PUBLIC, className, EClassType.CLASS)

        /*
        @Bean
    public IntegrationFlow processFileChannelFlow() {
        return IntegrationFlows.from(processFileChannel())
                .handle("processOrder", "prepare")
                .get();
    }
         */



        val method: JMethod = webService.method(JMod.PUBLIC, org.springframework.integration.dsl.IntegrationFlow::class.java, method)
        method.annotate(Bean::class.java)

        val body = method.body()

        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "service")
        codeModel.build(file)
    }
}