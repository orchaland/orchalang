package orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator

import org.jdom2.Element
import org.jdom2.Namespace
import java.util.HashMap


open class MessageTranslator : Bean() {

    fun translate(translationExpression: String): Element {

        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")
        val transformer = Element("transformer", namespace)
        //transformer.setAttribute("id", "transformer-"+instructionNode.inputName+"-id")
        //transformer.setAttribute("input-channel", instructionNode.inputName + "Transformer")
        //transformer.setAttribute("output-channel", outputChannel)
        transformer.setAttribute("expression", translationExpression)
        return transformer

    }

    fun objectToString(): Element {
        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")
        return Element("object-to-string-transformer", namespace)
    }

    fun objectToJson(): Element {

        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")
        return Element("object-to-json-transformer", namespace)
    }

    fun jsonToObject(type: String): Element {

        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")

        val element = Element("json-to-object-transformer", namespace)
        element.setAttribute("type", type)

        return element
    }

    /*fun objectToApplicationTransformer(integrationNode: IntegrationNode): Element {

        val instruction = integrationNode.instruction

        //val applicationName = instruction.springBean.name
        //val outputServiceChannel = applicationName + "ServiceAcivatorOutput"

        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")

        val transformer = Element("transformer", namespace)
        //transformer.setAttribute("id", "transformer-$outputServiceChannel-id")
        //transformer.setAttribute("input-channel", outputServiceChannel)
        //transformer.setAttribute("output-channel", integrationNode.outputName)
        transformer.setAttribute("method", "transform")

        val properties = object : HashMap<String, String>() {
            init {
                put("application", "applicationName")
            }
        }

        val beanElement = beanWithRef("orcha.lang.compiler.referenceimpl.xmlgenerator.impl.ObjectToApplicationTransformer", properties)

        transformer.addContent(beanElement)

        /*if (integrationNode.options != null && integrationNode.options.eventSourcing != null) {
            if (integrationNode.options.eventSourcing.joinPoint === JoinPoint.after || integrationNode.options.eventSourcing.joinPoint === JoinPoint.beforeAndAfter) {
                val adviceChain = Element("request-handler-advice-chain", namespace)
                transformer.addContent(adviceChain)
                val eventSourcingElement = eventSourcing(integrationNode.options.eventSourcing)
                adviceChain.addContent(eventSourcingElement)
            }
        }*/

        return transformer

    }

    fun applicationsListToObjectsListTransformer(integrationNode: IntegrationNode): Element {

        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")

        //val outputChannel = integrationNode.inputName + "AggregatorOutput"

        val transformer = Element("transformer", namespace)
        //transformer.setAttribute("id", "transformer-$outputChannel-id")
        //transformer.setAttribute("input-channel", outputChannel)
        //transformer.setAttribute("output-channel", integrationNode.outputName)
        transformer.setAttribute("method", "transform")

        val properties = HashMap<String, String>()

        val beanElement = beanWithValue("orcha.lang.compiler.referenceimpl.xmlgenerator.impl.ApplicationsListToObjectsListTransformer", properties)

        transformer.addContent(beanElement)

        return transformer

    }

    fun applicationToObjectTransformer(integrationNode: IntegrationNode): Element {

        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")

        //val outputChannel = integrationNode.inputName + "AggregatorOutput"

        val transformer = Element("transformer", namespace)
        //transformer.setAttribute("id", "transformer-$outputChannel-id")
        //transformer.setAttribute("input-channel", outputChannel)
        //transformer.setAttribute("output-channel", integrationNode.outputName)
        transformer.setAttribute("method", "transform")

        val properties = HashMap<String, String>()

        val beanElement = beanWithValue("orcha.lang.compiler.referenceimpl.xmlgenerator.impl.ApplicationToObjectTransformer", properties)

        transformer.addContent(beanElement)

        return transformer

    }*/

}
