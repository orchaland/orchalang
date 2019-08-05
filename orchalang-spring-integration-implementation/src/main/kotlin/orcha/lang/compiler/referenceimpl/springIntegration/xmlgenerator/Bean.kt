package orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator

import org.jdom2.Element
import org.jdom2.Namespace
import java.util.HashMap

open class Bean {

    fun bean(fullClassName: String): Element {

        val properties = HashMap<String, String>()
        return beanWithValue(fullClassName, properties)

    }

    fun bean(id: String, fullClassName: String): Element {

        val properties = HashMap<String, String>()
        return beanWithValue(id, fullClassName, properties)

    }

    fun beanWithValue(fullClassName: String, properties: Map<String, String>): Element {

        val namespace = Namespace.getNamespace("", "http://www.springframework.org/schema/beans")

        val bean = Element("bean", namespace)
        bean.setAttribute("class", fullClassName)

        properties.forEach { (k, v) ->
            val property = Element("property", namespace)
            property.setAttribute("name", k)
            property.setAttribute("value", v)
            bean.addContent(property)
        }

        return bean

    }

    fun beanWithValue(id: String, fullClassName: String, properties: Map<String, String>): Element {

        val namespace = Namespace.getNamespace("", "http://www.springframework.org/schema/beans")

        val bean = this.beanWithValue(fullClassName, properties)
        bean.setAttribute("id", id)

        return bean

    }

    fun beanWithRef(fullClassName: String, properties: Map<String, String>): Element {

        val namespace = Namespace.getNamespace("", "http://www.springframework.org/schema/beans")

        val bean = Element("bean", namespace)
        bean.setAttribute("class", fullClassName)

        properties.forEach { (k, v) ->
            val property = Element("property", namespace)
            property.setAttribute("name", k)
            property.setAttribute("ref", v)
            bean.addContent(property)
        }


        return bean

    }

}
