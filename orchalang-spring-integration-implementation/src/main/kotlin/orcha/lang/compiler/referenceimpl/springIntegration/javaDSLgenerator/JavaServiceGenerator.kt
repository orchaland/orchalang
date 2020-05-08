package orcha.lang.compiler.referenceimpl.springIntegration.javaDSLgenerator

import com.helger.jcodemodel.*
import org.springframework.context.annotation.Bean
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.dsl.Pollers
import org.springframework.messaging.MessageChannel
import java.io.File
import java.io.Serializable


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

        /*@Bean
        public IntegrationFlow fileReadingFlow() {
            return IntegrationFlows.from(Files.inboundAdapter(new File(".\\files")).patternFilter("*.txt"),
            a -> a.poller(Pollers.fixedDelay(1000))).transform(Files.toStringTransformer()).channel("processFileChannel").get();
        }*/



        var method: JMethod = webService.method(JMod.PUBLIC, org.springframework.integration.dsl.IntegrationFlow::class.java, method)
        method.annotate(Bean::class.java)

        var body = method.body()

        val fromInvoke = codeModel.ref(IntegrationFlows::class.java).staticInvoke("from")

        val inboundAdapterInvoke: JInvocation = codeModel.ref(org.springframework.integration.file.dsl.Files::class.java).staticInvoke("inboundAdapter")
        inboundAdapterInvoke.arg(JExpr._new(codeModel.ref(File::class.java)).arg("." + File.separator + "files"))
        val patternFilterInvoke = JExpr.invoke(inboundAdapterInvoke, "patternFilter").arg("*.txt")
        fromInvoke.arg(patternFilterInvoke)


        val holder = JVar(JMods.forVar(0), codeModel.ref(Any::class.java), "a", null)
        val aLambda = JLambda()
        val arr = aLambda.addParam("a")
        val setBody: JBlock = aLambda.body()

        val fixedDelayInvoke = codeModel.ref(Pollers::class.java).staticInvoke("fixedDelay")
        fixedDelayInvoke.arg(1000)

        setBody.add(JExpr.invoke(codeModel, holder, "poller").arg(fixedDelayInvoke))

        fromInvoke.arg(aLambda)

        val transformInvoke = JExpr.invoke(fromInvoke, "transform")
        val toStringTransformerInvoke: JInvocation = codeModel.ref(org.springframework.integration.file.dsl.Files::class.java).staticInvoke("toStringTransformer")
        transformInvoke.arg(toStringTransformerInvoke)

        val channelInvoke = JExpr.invoke(transformInvoke, "channel").arg("processFileChannel")

        val getInvoke = JExpr.invoke(channelInvoke, "get")
        body._return(getInvoke)

        /*@Bean
        public MessageChannel processFileChannel() {
            return new DirectChannel();
        }*/
        method = webService.method(JMod.PUBLIC, MessageChannel::class.java, "processFileChannel")
        method.annotate(Bean::class.java)

        body = method.body()

        val newChannelInvoque = JExpr._new(codeModel.ref(DirectChannel::class.java))
        body._return(newChannelInvoque)

        //val jc: JDefinedClass = codeModel ._class("ProcessOrder")
       //val methode= jc.method(JMod.PUBLIC, codeModel.VOID, "prepare")
       // methode.param(Order.type(), order.name());
   /*val className1 = "com.example.gettingStarted." + "Order"
        val orderapp= codeModel._class(JMod.PUBLIC, className1, EClassType.CLASS)
        orderapp._implements(Serializable::class.java)
        val product: JFieldVar = orderapp.field(JMod.NONE,String::class.java, "product")
       val id: JFieldVar = orderapp.field(JMod.NONE,Integer::class.java, "id")
        val const: JMethod = orderapp.method(JMod.PUBLIC, codeModel.VOID, "getProduct")
        const.param(product.type(),product.name())
        const.body().assign(JExpr._this().ref(product.name()), JExpr.ref(product.name()))
        const.body().assign(JExpr._this().ref(id.name()), JExpr.ref(id.name()))
        orderapp.constructor(JMod.PUBLIC).javadoc().add("Creates a new " + orderapp.name() + ".")
         val getter: JMethod = orderapp.method(JMod.PUBLIC, product.type(), "getProduct")
        getter.body()._return(product)
        val getterid: JMethod= orderapp.method(JMod.PUBLIC, id.type(), "getId");
        getterid.body()._return(id);
        val setterid: JMethod= orderapp.method(JMod.PUBLIC, id.type(), "setId");
        setterid.body().assign(JExpr._this().ref(id.name()), JExpr.ref(id.name()))

        val setp: JMethod = orderapp.method(JMod.PUBLIC, product.type(), "setProduct")
        setp.body().assign(JExpr._this().ref(product.name()), JExpr.ref(product.name()))
        val tostring: JMethod= orderapp.method(JMod.PUBLIC, String::class.java, "toString");
        tostring.annotate(Override::class.java)
        */



        val file = File("." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source")
        codeModel.build(file)
    }
}