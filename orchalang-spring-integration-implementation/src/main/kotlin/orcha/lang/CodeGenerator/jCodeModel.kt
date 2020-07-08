package orcha.lang.CodeGenerator

import com.helger.jcodemodel.*
import com.helger.jcodemodel.JExpr.FALSE
import com.helger.jcodemodel.JExpr._super
import orcha.lang.compiler.referenceimpl.*
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication
import orcha.lang.configuration.Application
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import java.io.File


class jCodeModel {
        fun generate() {
            val codeModel = JCodeModel()
            //  var orchaMetadata: OrchaMetadata = OrchaMetadata();
            //val classtitle = orchaMetadata.title + "configuration"
            val className = "essai." + "OrchaCompilerApplication"
            var  generat =codeModel._class(JMod.PUBLIC,className , EClassType.CLASS)
            //var  generat = codeModel._class(classtitle)

            generat.annotate(Configuration::class.java)
            val jAnnotation: JAnnotationUse = generat.annotate(SpringBootApplication::class.java)
            jAnnotation.paramArray("scanBasePackages", "orchalang");
          //  generat.annotate(SpringBootApplication::class.java).paramArray("scanBasePackages","orchalang")
            var method: JMethod = generat.method(JMod.PUBLIC, IntegrationFlow::class.java, "orchaProgramSourceFlow")
            method.annotate(Bean::class.java)
            var body = method.body()
            val fromInvoke: JInvocation = codeModel.ref(IntegrationFlows::class.java).staticInvoke("from")
            val inboundAdapterInvoke: JInvocation = codeModel.ref(org.springframework.integration.file.dsl.Files::class.java).staticInvoke("inboundAdapter")
            inboundAdapterInvoke.arg(JExpr._new(codeModel.ref(File::class.java)).arg("." + File.separator + "files"))
            val patternFilterInvoke = JExpr.invoke(inboundAdapterInvoke, "patternFilter").arg("*.orcha")
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

            method = generat.method(JMod.NONE, PreprocessingImpl::class.java,"preprocessing")
            //method.annotate(Bean::class.java)
            method.annotate(Bean::class.java).param("name","preprocessingForOrchaCompiler")
            body = method.body()
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
           method.annotate(DependsOn::class.java).param("{'sendInstruction' , 'whenInstruction' }");
            //    @DependsOn({"whenInstruction", "sendInstruction"})??????????? mech narj3elha
            //param("sendInstruction","whenInstruction")
            body = method.body()
            val newLexicalAnalysisInvoque = JExpr._new(codeModel.ref(LexicalAnalysisImpl::class.java))
            body._return(newLexicalAnalysisInvoque)

            method = generat.method(JMod.NONE,  MessageToApplication::class.java, "preprocessingMessageToApplication")
            method.annotate(Bean::class.java)
            body = method.body()


            val newapreprocessingMessageInvoque = JExpr._new(codeModel.ref( MessageToApplication::class.java)).arg("Application.State.TERMINATED").arg("preprocessing")
            //.arg(Application.State.TERMINATED).arg(postprocessing.name)??????????????????????????????????mech narj3elha

            //  @Bean
            //    MessageToApplication preprocessingMessageToApplication() {
            //        return new MessageToApplication(Application.State.TERMINATED, "preprocessing");
            //    }
            body._return(newapreprocessingMessageInvoque)
            method = generat.method(JMod.NONE,  ApplicationToMessage::class.java, "applicationToMessage")
            method.annotate(Bean::class.java)
            body = method.body()
            val newapplicationToMessageInvoque = JExpr._new(codeModel.ref( ApplicationToMessage::class.java))
            body._return(newapplicationToMessageInvoque)
            method = generat.method(JMod.PUBLIC, IntegrationFlow::class.java, "orchaProgramSourceChannel")
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

            val aggregateInvoke1 =JExpr.invoke(handleInvoke2,"aggregate")

            val holder3 = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "a", null)
            val aLambda3 = JLambda()
            val arr3= aLambda3.addParam("a")
            val setBody3: JBlock = aLambda3.body()
            val relationIdExceptionInvoke=JExpr.invoke(codeModel,holder3,"releaseExpression").arg("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )")
            val correlationStrategyInvoke=JExpr.invoke(relationIdExceptionInvoke,"correlationStrategy").arg("headers['messageID']")
            setBody3.add(correlationStrategyInvoke)
            aggregateInvoke1.arg(aLambda3)
            val transformInvoke=JExpr.invoke(aggregateInvoke1,"transform").arg("payload.?[name=='preprocessing']")
            val handlerinvoke =JExpr.invoke(transformInvoke,"handle").arg(applicationToMessageInvoke).arg("transform")
            val logInvoke1 = JExpr.invoke(handlerinvoke, "log")
            body._return(logInvoke1)







            val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source" )
            codeModel.build(file)

        }
    }
