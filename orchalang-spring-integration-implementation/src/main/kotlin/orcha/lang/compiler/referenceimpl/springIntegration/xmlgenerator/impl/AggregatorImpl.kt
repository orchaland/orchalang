package orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator.impl

import orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator.Aggregator
import orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator.MessageTranslator
import org.jdom2.Element
import org.jdom2.Namespace

class AggregatorImpl : MessageTranslator(), Aggregator{

    override fun aggregate(releaseExpression: String): Element {

        //val rootElement = document!!.getRootElement()

        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")


        //val sameEvent = integrationNode.options.sameEvent
        val sameEvent = false

        //val instruction = integrationNode.instruction

        //val outputChannel = integrationNode.inputName + "AggregatorOutput"

        /*if (integrationNode.options == null) {
            integrationNode.options = QualityOfServicesOptions(false)
        }*/

        val aggregatorElement = Element("aggregator", namespace)
        //aggregatorElement.setAttribute("id", "aggregator-" + integrationNode.inputName + "-id")

        //aggregatorElement.setAttribute("input-channel", integrationNode.inputName)

        //aggregatorElement.setAttribute("output-channel", integrationNode.inputName + "Transformer")
        aggregatorElement.setAttribute("release-strategy-expression", releaseExpression)

        if (sameEvent == true) {

            aggregatorElement.setAttribute("correlation-strategy-expression", "headers['messageID']")

        } else {

            aggregatorElement.setAttribute("correlation-strategy-expression", "0")

        }

        //rootElement.addContent(aggregatorElement)


        /*val transformer = Element("transformer", namespace)
        transformer.setAttribute("id", "transformer-" + integrationNode.inputName + "-id")
        transformer.setAttribute("input-channel", integrationNode.inputName + "Transformer")
        transformer.setAttribute("output-channel", outputChannel)
        transformer.setAttribute("expression", transformerExpression)

        rootElement.addContent(transformer)*/

        /*if (isMultipleArgumentsInExpression == true) {

            val applicationsListToObjectsListElement = applicationsListToObjectsListTransformer(integrationNode)
            rootElement.addContent(applicationsListToObjectsListElement)

        } else {

            val applicationToObjectElement = applicationToObjectTransformer(integrationNode)
            rootElement.addContent(applicationToObjectElement)

        }*/

        /*if (integrationNode.options != null && integrationNode.options.queue != null) {

            val queueElement = queue(integrationNode.outputName, integrationNode.options.queue)
            rootElement.addContent(queueElement)

        }*/

        return aggregatorElement


    }
}
