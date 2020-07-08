package orcha.lang.compiler.referenceimpl.springIntegration

import com.helger.jcodemodel.*
import orcha.lang.compiler.Preprocessing
import orcha.lang.compiler.referenceimpl.*
import orcha.lang.configuration.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import java.io.File
class OutputCodeGeneratorImpl : OutputCodeGenerator {
    val codeModel = JCodeModel()
    //  var orchaMetadata: OrchaMetadata = OrchaMetadata();
    //val classtitle = orchaMetadata.title + "configuration"
    val className = "essai." + "orchaCompilerApplication"
    var  generat =codeModel._class(JMod.PUBLIC,className , EClassType.CLASS)
    //var  generat = codeModel._class(classtitle)
    override fun inputAdapter(adapter: ConfigurableProperties) {
        when(adapter){
            is InputFileAdapter -> {
                val inputFileAdapter: InputFileAdapter = adapter as InputFileAdapter
                log.info("Generation of the output code for " + inputFileAdapter)
                generat.annotate(Configuration::class.java)
                generat.annotate(SpringBootApplication::class.java).paramArray("scanBasePackages","orchalang")
                val method: JMethod = generat.method(JMod.PUBLIC, IntegrationFlow::class.java, "orchaProgramSourceFlow")
                method.annotate(Bean::class.java)
                val body = method.body()
                val fromInvoke: JInvocation = codeModel.ref(IntegrationFlows::class.java).staticInvoke("from")
                val inboundAdapterInvoke: JInvocation = codeModel.ref(org.springframework.integration.file.dsl.Files::class.java).staticInvoke("inboundAdapter")
                inboundAdapterInvoke.arg(JExpr._new(codeModel.ref(File::class.java)).arg(adapter.directory))
                val patternFilterInvoke = JExpr.invoke(inboundAdapterInvoke, "patternFilter").arg(adapter.filenamePattern)
                fromInvoke.arg(patternFilterInvoke)
                val holder = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "a", null)
                val aLambda = JLambda()
                val arr = aLambda.addParam("a")
                val setBody: JBlock = aLambda.body()
                val fixedDelayInvoke: JInvocation = codeModel.ref(Pollers::class.java).staticInvoke("fixedDelay")
                fixedDelayInvoke.arg(1000)
                setBody.add(JExpr.invoke(codeModel, holder, "poller").arg(fixedDelayInvoke))
                fromInvoke.arg(aLambda)
                val logInvoke = JExpr.invoke(fromInvoke,"log")
                val channelInvoke = JExpr.invoke(logInvoke, "channel").arg("orchaProgramSourceChannel.input")
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
    override fun serviceActivator(adapter: ConfigurableProperties)
    {
        when(adapter){
            is JavaServiceAdapter -> {
                val javaServiceAdapter: JavaServiceAdapter = adapter as JavaServiceAdapter
                log.info("Generation of a service activator for " + javaServiceAdapter)

                var method = generat.method(JMod.NONE, PreprocessingImpl::class.java,"preprocessing")
                //method.annotate(Bean::class.java)
                method.annotate(Bean::class.java).param("name","preprocessingForOrchaCompiler")
                var  body = method.body()
                val newPreprocessingInvoque = JExpr._new(codeModel.ref(PreprocessingImpl::class.java))
                body._return(newPreprocessingInvoque)
                 method = generat.method(JMod.NONE, SyntaxAnalysisImpl::class.java, "syntaxAnalysis")
                //method.annotate(Bean::class.java)
                method.annotate(Bean::class.java).param("name","syntaxAnalysisForOrchaCompiler")
                body = method.body()
                val newSyntaxAnalysisInvoque = JExpr._new(codeModel.ref(SyntaxAnalysisImpl::class.java))
                body._return(newSyntaxAnalysisInvoque)
                method = generat.method(JMod.NONE, SemanticAnalysisImpl::class.java, "semanticAnalysis")
                method.annotate(Bean::class.java).param("name","semanticAnalysisForOrchaCompiler")
                body = method.body()
                val newSemanticAnalysisInvoque = JExpr._new(codeModel.ref(SemanticAnalysisImpl::class.java))
                body._return(newSemanticAnalysisInvoque)
                method = generat.method(JMod.NONE, PostprocessingImpl::class.java, "postprocessing")
                method.annotate(Bean::class.java).param("name","postprocessingForOrchaCompiler")
                body = method.body()
                val newPostprocessingInvoque = JExpr._new(codeModel.ref(PostprocessingImpl::class.java))
                body._return(newPostprocessingInvoque)

                method = generat.method(JMod.NONE, LexicalAnalysisImpl::class.java, "lexicalAnalysis")
                method.annotate(Bean::class.java).param("name","lexicalAnalysisForOrchaCompiler")
                method.annotate(DependsOn::class.java)
                //    @DependsOn({"whenInstruction", "sendInstruction"})??????????? mech narj3elha
                        //param("sendInstruction","whenInstruction")
                body = method.body()
                val newLexicalAnalysisInvoque = JExpr._new(codeModel.ref(LexicalAnalysisImpl::class.java))
                body._return(newLexicalAnalysisInvoque)

                method = generat.method(JMod.NONE,  ApplicationToMessage::class.java, "preprocessingMessageToApplication")
                method.annotate(Bean::class.java)
                body = method.body()
                val newapreprocessingMessageInvoque = JExpr._new(codeModel.ref( ApplicationToMessage::class.java))
                        //.arg(Application.State.TERMINATED).arg(postprocessing.name)??????????????????????????????????mech narj3elha
                body._return(newapreprocessingMessageInvoque)
                method = generat.method(JMod.NONE,  ApplicationToMessage::class.java, "applicationToMessage")
                method.annotate(Bean::class.java)
                body = method.body()
                val newapplicationToMessageInvoque = JExpr._new(codeModel.ref( ApplicationToMessage::class.java))
                body._return(newapplicationToMessageInvoque)
                /// @Bean
                // public IntegrationFlow orchaProgramSourceChannel() {
                  //  return f -> f.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
                    //.handle("preprocessingForOrchaCompiler", "process")
                      //  .handle(preprocessingMessageToApplication(), "transform")
                       // .aggregate(a -> a.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )").correlationExpression("headers['messageID']"))
                   // .transform("payload.?[name=='preprocessing']")
                       // .handle(applicationToMessage(), "transform")
                       // .log();
               // }
                method = generat.method(JMod.PUBLIC, IntegrationFlow::class.java, "orchaProgramSourceChannel")
                method.annotate(Bean::class.java)
                body = method.body()
                val holder1 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "f", null)
                val aLambda1 = JLambda()
                val arr1= aLambda1.addParam("f")
                val setBody1: JBlock = aLambda1.body()
                //val enrichHeadersInvoke = JExpr.invoke(holder1,"enrichHeaders")
                val holder3 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "h", null)
                val aLambda3 = JLambda()
                val arr3= aLambda3.addParam("h")
                val setBody3: JBlock = aLambda3.body()
                setBody3.add(JExpr.invoke(codeModel,holder3,"headerExpression").arg("messageID").arg("headers['id'].toString()"))
                setBody1.add(JExpr.invoke(codeModel,holder1,"enrichHeaders").arg(aLambda3))
              val preprocessingMessageToApplicationInvoke=JExpr.invoke("preprocessingMessageToApplication")
                val applicationToMessageInvoke=JExpr.invoke("applicationToMessage")
                val handlerInvoke1=JExpr.invoke(codeModel,holder1,"handle").arg("preprocessingForOrchaCompiler").arg("process")
                val handleInvoke2=JExpr.invoke(handlerInvoke1,"handle").arg(preprocessingMessageToApplicationInvoke).arg("transform")
                val transformInvoke=JExpr.invoke(handleInvoke2,"transform").arg("payload.?[name=='preprocessing']")


                val handlerinvoke =JExpr.invoke(transformInvoke,"handle").arg(applicationToMessageInvoke).arg("transform")
                 val logInvoke1 = JExpr.invoke(handlerinvoke, "log")
                body._return(logInvoke1)


            }
        }
        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source" )
        codeModel.build(file)


    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputCodeGeneratorImpl::class.java)

    }

}