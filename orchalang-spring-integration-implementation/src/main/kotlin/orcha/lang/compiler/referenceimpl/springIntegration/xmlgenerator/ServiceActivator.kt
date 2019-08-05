package orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator

import org.jdom2.Element
import org.jdom2.Namespace

open class ServiceActivator {

    fun serviceActivator(expression: String): Element {

        val namespace = Namespace.getNamespace("int", "http://www.springframework.org/schema/integration")

        val element = Element("service-activator", namespace)
        element.setAttribute("expression", expression)

        return element
    }

}