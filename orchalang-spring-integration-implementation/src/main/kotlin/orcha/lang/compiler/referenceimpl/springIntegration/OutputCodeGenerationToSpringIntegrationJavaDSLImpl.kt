package orcha.lang.compiler.referenceimpl.springIntegration

import com.helger.jcodemodel.*
import com.helger.jcodemodel.writer.FileCodeWriter
import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.syntax.ComputeInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Bean
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import org.springframework.orm.jpa.EntityManagerFactoryInfo
import java.io.File
import javax.persistence.EntityManagerFactory


class OutputCodeGenerationToSpringIntegrationJavaDSLImpl : OutputCodeGenerationToSpringIntegrationJavaDSL {
    var generatedClass : JDefinedClass ? = null
    val codeModel = JCodeModel()
    override fun orchaMetadata(orchaMetadata: OrchaMetadata) {
        val className =  orchaMetadata.titleAsCapitalizedConcatainedString+"Application"
        log.info("Generated class name: " + className)
        generatedClass = codeModel._class(JMod.PUBLIC, className , EClassType.CLASS)
      generatedClass!!.annotate(SpringBootApplication::class.java)
    }
    override fun inputAdapter(eventHandler: EventHandler, nextIntegrationNodes: List<IntegrationNode>) {
        val adapter = eventHandler.input!!.adapter
        when(adapter){
            is DatabaseAdapter -> {
                val entityManagerFactory: JFieldVar = generatedClass!!.field(JMod.PRIVATE, EntityManagerFactory::class.java, "entityManagerFactory")
                entityManagerFactory.annotate(Autowired::class.java)
                val inputAdapter: DatabaseAdapter = adapter as DatabaseAdapter
                log.info("Generation of the output code for " + inputAdapter)
                var method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, eventHandler.name +"Flow")
                method.annotate(Bean::class.java)
                var body =method.body()
                val fromInvoke = codeModel.ref(IntegrationFlows::class.java).staticInvoke("from")
                val inboundAdapterInvoke: JInvocation = codeModel.ref(org.springframework.integration.jpa.dsl.Jpa::class.java).staticInvoke("inboundAdapter")
                val thisInvoke = JExpr.ref( "this")
                val entityManagerFactoryInvoke = JExpr.ref( thisInvoke,"entityManagerFactory")
                inboundAdapterInvoke.arg(entityManagerFactoryInvoke)
                val entityClassinvoke=JExpr.invoke(inboundAdapterInvoke,"entityClass")
                val studentdomainref=JExpr.ref("StudentDomain")
                val classref=JExpr.ref(studentdomainref,"class")
                entityClassinvoke.arg(classref)
                val maxResultsinvoke=JExpr.invoke(entityClassinvoke,"maxResults")
                val valinref=JExpr.ref("1")
                maxResultsinvoke.arg(valinref)
                val expectSingleResultinvoke=JExpr.invoke(maxResultsinvoke,"expectSingleResult")
                val trueinref=JExpr.ref("true")
                expectSingleResultinvoke.arg(trueinref)
                fromInvoke.arg(expectSingleResultinvoke)
                val holder = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "e", null)
                val aLambda = JLambda()
                val arr = aLambda.addParam("e")
                val setBody: JBlock = aLambda.body()
                val pollerinvoke= JExpr.invoke(codeModel, holder, "poller")
                setBody.add(pollerinvoke)
                fromInvoke.arg(aLambda)
                val holder11 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "p", null)
                val aLambda11 = JLambda()
                val arr11 = aLambda11.addParam("p")
                val setBody11: JBlock = aLambda11.body()
                val fixedDelayInvoke=JExpr.invoke(codeModel, holder11,"fixedDelay").arg(5000)
                pollerinvoke.arg(aLambda11)
                setBody11.add(fixedDelayInvoke)
                val enrichHeadersInvoke =JExpr.invoke(codeModel,fromInvoke,"enrichHeaders")
                val holder2 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "h", null)
                val aLambda2 = JLambda()
                val arr2= aLambda2.addParam("h")
                val setBody2: JBlock = aLambda2.body()
                setBody2.add(JExpr.invoke(codeModel,holder2,"headerExpression").arg("messageID").arg("headers['id'].toString()"))
                var channelInvoke: JInvocation? =null
                val nextIntegrationNode = nextIntegrationNodes[0]
                when(nextIntegrationNode.instruction){
                    is ComputeInstruction -> {
                        val compute: ComputeInstruction = nextIntegrationNode.instruction as ComputeInstruction
                        val application: Application = compute.configuration as Application
                        channelInvoke = JExpr.invoke(enrichHeadersInvoke.arg(aLambda2), "channel").arg(application.name + "Channel.input")
                    }
                }
                val logInvoke = JExpr.invoke(channelInvoke, "log")
                val getInvoke = JExpr.invoke(logInvoke, "get")
                body._return(getInvoke)
            }
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

                val enrichHeadersInvoke =JExpr.invoke(codeModel,fromInvoke,"enrichHeaders")
                val holder2 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "h", null)
                val aLambda2 = JLambda()
                val arr2= aLambda2.addParam("h")
                val setBody2: JBlock = aLambda2.body()
                setBody2.add(JExpr.invoke(codeModel,holder2,"headerExpression").arg("messageID").arg("headers['id'].toString()"))

                var channelInvoke: JInvocation? =null

                val nextIntegrationNode = nextIntegrationNodes[0]
                when(nextIntegrationNode.instruction){
                    is ComputeInstruction -> {
                        val compute: ComputeInstruction = nextIntegrationNode.instruction as ComputeInstruction
                        val application: Application = compute.configuration as Application
                        channelInvoke = JExpr.invoke(enrichHeadersInvoke.arg(aLambda2), "channel").arg(application.name + "Channel.input")
                    }
                }

                val getInvoke = JExpr.invoke(channelInvoke, "get")
                body._return(getInvoke)
            }
        }
    }
    override fun outputAdapter(eventHandler: EventHandler) {
        val adapter = eventHandler.output!!.adapter
        when(adapter){
            is DatabaseAdapter -> {
                val outputAdapter: DatabaseAdapter = adapter as DatabaseAdapter
                log.info("Generation of the output code for " + outputAdapter)
                val method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, eventHandler.name+"Channel")
                method.annotate(Bean::class.java)
                 val body =method.body()
                val holder5 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
                val aLambda5 = JLambda()
                val arr5= aLambda5.addParam("f")
                val setBody5: JBlock = aLambda5.body()
                val handleInvoke1 =JExpr.invoke(codeModel,holder5,"handle")
                setBody5.add(handleInvoke1)
                val outboundAdapterInvoke= codeModel.ref(org.springframework.integration.jpa.dsl.Jpa::class.java).staticInvoke("outboundAdapter")
                val thisInvoke1 = JExpr.ref( "this")
                val entityManagerFactoryInvoke1 = JExpr.ref( thisInvoke1,"entityManagerFactory")
                outboundAdapterInvoke.arg(entityManagerFactoryInvoke1)
                val entityClassinvoke1=JExpr.invoke( outboundAdapterInvoke,"entityClass")
                val studentdomainvoke1=JExpr.ref("StudentDomain")
                val classref1=JExpr.ref(studentdomainvoke1,"class")
                entityClassinvoke1.arg(classref1)
                val persistModeinvoke=JExpr.invoke(entityClassinvoke1,"persistMode")
                val PersistModeref=JExpr.ref("PersistMode")
                val PERSISTref=JExpr.ref(PersistModeref,"PERSIST")
                persistModeinvoke.arg(PERSISTref)
                handleInvoke1.arg(persistModeinvoke)
                val holder6 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "e", null)
                val aLambda6 = JLambda()
                val arr6 = aLambda6.addParam("e")
                val setBody6: JBlock = aLambda6.body()
                val transactionInvoke=JExpr.invoke(codeModel, holder6,"transactional")
                handleInvoke1.arg(aLambda6)
                setBody6.add(transactionInvoke)
                val logInvoke1 = JExpr.invoke(codeModel,aLambda5,"log")
                body._return(logInvoke1 )
            }
        }
    }
    override fun filter(expression: String) {
        log.info("Generation of a filter for the expression " + expression)
    }
    override fun serviceActivator(application: Application, nextIntegrationNodes: List<IntegrationNode>) {
        val adapter = application.input!!.adapter
        when(adapter){
            is JavaServiceAdapter -> {
                val javaServiceAdapter: JavaServiceAdapter = adapter as JavaServiceAdapter
                log.info("Generation of a service activator for " + javaServiceAdapter)
                val classe = Class.forName(javaServiceAdapter.javaClass)
                var method = generatedClass!!.method(JMod.NONE, classe, application.name)
                method.annotate(Bean::class.java).param("name",  application.name)
                var body = method.body()
                val serviceInvoque = JExpr._new(codeModel.ref(classe))
                body._return(serviceInvoque)
                method = generatedClass!!.method(JMod.NONE,  MessageToApplication::class.java, application.name + "MessageToApplication")
                method.annotate(Bean::class.java)
                body = method.body()
                val ApplicationInvoke=JExpr.ref("Application")
                val StateInvoke=JExpr.refthis(ApplicationInvoke,"State")
                val TERMINATEDInvoke=JExpr.refthis(StateInvoke,"TERMINATED")
               val newapreprocessingMessageInvoque = JExpr._new(codeModel.ref( MessageToApplication::class.java)).arg(TERMINATEDInvoke).arg(application.name)
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
                val messageToApplicationInvoke=JExpr.invoke(application.name + "MessageToApplication")
                val handlerInvoke1=JExpr.invoke(codeModel,holder1,"handle").arg(application.name).arg(javaServiceAdapter.method)
                setBody1.add(handlerInvoke1)
                val handleInvoke2=JExpr.invoke(codeModel,aLambda1,"handle").arg(messageToApplicationInvoke).arg("transform")
                var channelInvoke: JInvocation? = null
                val nextIntegrationNode = nextIntegrationNodes[0]
                when(nextIntegrationNode.instruction){
                    is WhenInstruction -> {
                        val whenInstruction: WhenInstruction = nextIntegrationNode.instruction as WhenInstruction
                        channelInvoke = JExpr.invoke(handleInvoke2, "channel").arg("aggregate" + whenInstruction.applicationsOrEventsAsCapitalizedConcatainedString + "Channel.input")
                    }
                }
                body._return(channelInvoke)
            }
        }
    }
    override fun aggregator(whenInstruction: WhenInstruction, nextIntegrationNodes: List<IntegrationNode>) {
        log.info("Generation of the output code for " + whenInstruction)
        val method = generatedClass!!.method(JMod.PUBLIC, IntegrationFlow::class.java, "aggregate" + whenInstruction.applicationsOrEventsAsCapitalizedConcatainedString + "Channel")
        method.annotate(Bean::class.java)
        val body = method.body()
        val holder1 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
        val aLambda1 = JLambda()
        val arr1= aLambda1.addParam("f")
        val aggregateInvoke1 =JExpr.invoke(codeModel,holder1,"aggregate")
        val setBody1: JBlock = aLambda1.body()
        val holder3 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "a", null)
        val aLambda3 = JLambda()
        val arr3= aLambda3.addParam("a")
        val setBody3: JBlock = aLambda3.body()
        setBody1.add(aggregateInvoke1)
        val relationIdExceptionInvoke=JExpr.invoke(codeModel,holder3,"releaseExpression").arg(whenInstruction.aggregationExpression)
        val correlationStrategyInvoke=JExpr.invoke(relationIdExceptionInvoke,"correlationStrategy").arg("headers['messageID']")
        setBody3.add(correlationStrategyInvoke)
        aggregateInvoke1.arg(aLambda3)
        val applicationName = whenInstruction.applicationsOrEvents[0].name
        val transformInvoke=JExpr.invoke(codeModel,aLambda1,"transform").arg("payload.?[name=='" + applicationName + "']")
        val applicationToMessageInvoke=JExpr.invoke("applicationToMessage")
        val handlerinvoke =JExpr.invoke(transformInvoke,"handle").arg(applicationToMessageInvoke).arg("transform")
        var channelInvoke: JInvocation? = null
        val nextIntegrationNode = nextIntegrationNodes[0]
        when(nextIntegrationNode.instruction){
            is ComputeInstruction -> {
                val computeInstruction: ComputeInstruction = nextIntegrationNode.instruction as ComputeInstruction
                channelInvoke = JExpr.invoke(handlerinvoke, "channel").arg(computeInstruction.application + "Channel.input")
            }
        }
        body._return(channelInvoke)
    }
    override fun export() {

                val method = generatedClass!!.method(JMod.PUBLIC or JMod.STATIC, codeModel.VOID, "main")

                val springRef = codeModel.ref("String")

                method.param(JMod.NONE, springRef.array(), "args")
                val body = method.body()
                val springInvoke = JExpr._new(codeModel.ref(SpringApplicationBuilder::class.java))
                val orchaInvoke = JExpr.ref("OrchaCompilerApplication")
                val classInvoke = JExpr.refthis(orchaInvoke, "class")
                springInvoke.arg(classInvoke)
                val webInvoke = JExpr.invoke(springInvoke, "web")
                val WebApplicationTypeInvoke = JExpr.ref("WebApplicationType")
                val NONEInvoke = JExpr.refthis(WebApplicationTypeInvoke, "NONE")
                webInvoke.arg(NONEInvoke)
                val runInvoke = JExpr.invoke(webInvoke, "run")
                val argsInvoke = JExpr.ref("args")
                runInvoke.arg(argsInvoke)
                body.add(runInvoke)

        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source" )
        log.info("Export generated class to: " + file.absolutePath)
        codeModel.build(FileCodeWriter(file))
    }




    companion object {
        private val log = LoggerFactory.getLogger(OutputCodeGenerationToSpringIntegrationJavaDSLImpl::class.java)
    }

}