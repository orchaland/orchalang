package orcha.lang.compiler.referenceimpl.springIntegration

import com.helger.jcodemodel.*
import com.helger.jcodemodel.writer.FileCodeWriter
import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.referenceimpl.PreprocessingImpl
import orcha.lang.compiler.syntax.ComputeInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.*
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers

class OutputCodeGenerationToSpringIntegrationJavaDSLImpl : OutputCodeGenerationToSpringIntegrationJavaDSL {

    var generatedClass : JDefinedClass ? = null
    val codeModel = JCodeModel()

    override fun orchaMetadata(orchaMetadata: OrchaMetadata) {

        val className = orchaMetadata.domainAsCapitalizedConcatainedString!!.decapitalize() + "." + orchaMetadata.titleAsCapitalizedConcatainedString + "Application"

        log.info("Generated class name: " + className)

        generatedClass = codeModel._class(JMod.PUBLIC, className , EClassType.CLASS)

        generatedClass!!.annotate(Configuration::class.java)

        val jAnnotation: JAnnotationUse = generatedClass!!.annotate(SpringBootApplication::class.java)
        jAnnotation.paramArray("scanBasePackages", orchaMetadata.domain);
    }

    override fun inputAdapter(eventHandler: EventHandler, nextIntegrationNodes: List<IntegrationNode>) {
        val adapter = eventHandler.input!!.adapter
        when(adapter){
            is InputFileAdapter -> {
                val inputFileAdapter: InputFileAdapter = adapter as InputFileAdapter
                log.info("Generation of the output code for " + inputFileAdapter)
                var method: JMethod = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, eventHandler.name + "Flow")
                method.annotate(Bean::class.java)
                var body = method.body()
                val fromInvoke: JInvocation = codeModel.ref(IntegrationFlows::class.java).staticInvoke("from")
                val inboundAdapterInvoke: JInvocation = codeModel.ref(org.springframework.integration.file.dsl.Files::class.java).staticInvoke("inboundAdapter")
                inboundAdapterInvoke.arg(JExpr._new(codeModel.ref(File::class.java)).arg(inputFileAdapter.directory))
                val patternFilterInvoke = JExpr.invoke(inboundAdapterInvoke, "patternFilter").arg(inputFileAdapter.filenamePattern)
                fromInvoke.arg(patternFilterInvoke)
                val holder = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "a", null)
                val aLambda = JLambda()
                val arr = aLambda.addParam("a")
                val setBody: JBlock = aLambda.body()
                val fixedDelayInvoke: JInvocation = codeModel.ref(Pollers::class.java).staticInvoke("fixedDelay")
                fixedDelayInvoke.arg(1000)
                setBody.add(JExpr.invoke(codeModel, holder, "poller").arg(fixedDelayInvoke))
                fromInvoke.arg(aLambda)

                var channelInvoke: JInvocation? =null

                val nextIntegrationNode = nextIntegrationNodes[0]
                when(nextIntegrationNode.instruction){
                    is ComputeInstruction -> {
                        val compute: ComputeInstruction = nextIntegrationNode.instruction as ComputeInstruction
                        val application: Application = compute.configuration as Application
                        channelInvoke = JExpr.invoke(fromInvoke, "channel").arg(application.name + "Channel.input")
                    }
                }

                val getInvoke = JExpr.invoke(channelInvoke, "get")
                body._return(getInvoke)
            }
        }
    }

    override fun outputAdapter(adapter: ConfigurableProperties) {
        when(adapter){
            is OutputFileAdapter -> {
                val outputFileAdapter: OutputFileAdapter = adapter as OutputFileAdapter
                log.info("Generation of the output code for " + outputFileAdapter)
            }
        }
    }

    override fun filter(expression: String) {
        log.info("Generation of a filter for the expression " + expression)
    }

    override fun serviceActivator(application: Application, nextIntegrationNodes: List<IntegrationNode>) {
        println(nextIntegrationNodes)
        val adapter = application.input!!.adapter
        when(adapter){
            is JavaServiceAdapter -> {

                val javaServiceAdapter: JavaServiceAdapter = adapter as JavaServiceAdapter
                log.info("Generation of a service activator for " + javaServiceAdapter)
                val classe = Class.forName(javaServiceAdapter.javaClass)
                var method = generatedClass!!.method(JMod.NONE, classe, application.name)
                method.annotate(Bean::class.java).param("name","preprocessingForOrchaCompiler")
                var body = method.body()
                val serviceInvoque = JExpr._new(codeModel.ref(classe))
                body._return(serviceInvoque)

                method = generatedClass!!.method(JMod.NONE,  MessageToApplication::class.java, application.name + "MessageToApplication")
                method.annotate(Bean::class.java)
                body = method.body()


                val newapreprocessingMessageInvoque = JExpr._new(codeModel.ref( MessageToApplication::class.java)).arg("Application.State.TERMINATED").arg(application.name)
                //.arg(Application.State.TERMINATED).arg(postprocessing.name)??????????????????????????????????mech narj3elha

                //  @Bean
                //    MessageToApplication preprocessingMessageToApplication() {
                //        return new MessageToApplication(Application.State.TERMINATED, "preprocessing");
                //    }
                body._return(newapreprocessingMessageInvoque)
                method = generatedClass!!.method(JMod.NONE,  ApplicationToMessage::class.java, "applicationToMessage")
                method.annotate(Bean::class.java)
                body = method.body()
                val newapplicationToMessageInvoque = JExpr._new(codeModel.ref( ApplicationToMessage::class.java))
                body._return(newapplicationToMessageInvoque)
                method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, application.name + "Channel")
                method.annotate(Bean::class.java)
                body = method.body()

                val holder1 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
                val aLambda1 = JLambda()
                val arr1= aLambda1.addParam("f")
                val setBody1: JBlock = aLambda1.body()
                val enrichHeadersInvoke =JExpr.invoke(codeModel,holder1,"enrichHeaders")
                val holder2 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "h", null)
                val aLambda2 = JLambda()
                val arr2= aLambda2.addParam("h")
                val setBody2: JBlock = aLambda2.body()
                setBody2.add(JExpr.invoke(codeModel,holder2,"headerExpression").arg("messageID").arg("headers['id'].toString()"))
                setBody1.add(enrichHeadersInvoke.arg(aLambda2))
                val preprocessingMessageToApplicationInvoke=JExpr.invoke("preprocessingMessageToApplication")
                val applicationToMessageInvoke=JExpr.invoke("applicationToMessage")
                val handlerInvoke1=JExpr.invoke(codeModel,aLambda1,"handle").arg("preprocessingForOrchaCompiler").arg("process")
                val handleInvoke2=JExpr.invoke(handlerInvoke1,"handle").arg(preprocessingMessageToApplicationInvoke).arg("transform")
                val channelInvoke = JExpr.invoke(handleInvoke2, "channel").arg("aggregate" + application.name.capitalize() + "Channel.input")
                body._return(channelInvoke)
            }
        }
    }

    override fun aggregator(instruction: WhenInstruction) {
        log.info("Generation of the output code for " + instruction)
        val method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, "orchaProgramSourceChannel")
        method.annotate(Bean::class.java)
        val body = method.body()
    }

    override fun export() {
        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source" )
        log.info("Export generated class to: " + file.absolutePath)
        codeModel.build(FileCodeWriter(file))
    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputCodeGenerationToSpringIntegrationJavaDSLImpl::class.java)
    }

}