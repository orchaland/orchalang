package orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

interface Aggregator {

    fun aggregate(releaseExpression: String): Element

}
