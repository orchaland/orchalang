package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.OrchaProgram
import orcha.lang.compiler.OutputGeneration
import orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator.Aggregator
import orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator.MessageFilter
import orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator.ServiceActivator
import orcha.lang.compiler.referenceimpl.springIntegration.xmlgenerator.impl.AggregatorImpl
import orcha.lang.compiler.syntax.ComputeInstruction
import orcha.lang.compiler.syntax.ReceiveInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

class OutputGenerationToSpringIntegration : OutputGeneration, MessageFilter(), Aggregator {

    internal var aggregator: AggregatorImpl = AggregatorImpl()

    override fun aggregate(releaseExpression: String): Element {
        return aggregator.aggregate(releaseExpression)
    }

/*    internal lateinit var sourceCodeDirectory: File

    init {

        try {
            val directories = pathToBinaryCode.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            var pathToBinCode = "."
            for (directory in directories) {
                pathToBinCode = pathToBinCode + File.separator + directory
            }

            binaryCodeDirectory = File(pathToBinCode)

            val pathToBinary = binaryCodeDirectory.canonicalPath + File.separator + "orcha" + File.separator + "lang" + File.separator + "generated"

            var files = File(pathToBinary).listFiles()
            if (files != null) {
                for (file in files) {
                    log.info("Delete existing generated file: " + file.canonicalPath)
                    file.delete()
                }
            }

            val pathToSouresCode = "." + File.separator + "src" + File.separator + "main"
            sourceCodeDirectory = File(pathToSouresCode)


            var pathToSource = sourceCodeDirectory.canonicalPath + File.separator + "groovy" + File.separator + "orcha" + File.separator + "lang" + File.separator + "generated"

            files = File(pathToSource).listFiles()
            if (files != null) {
                for (file in files) {
                    log.info("Delete existing generated file: " + file.canonicalPath)
                    file.delete()
                }
            }

            pathToSource = sourceCodeDirectory.canonicalPath + File.separator + "java" + File.separator + "orcha" + File.separator + "lang" + File.separator + "generated"

            files = File(pathToSource).listFiles()
            if (files != null) {
                for (file in files) {
                    log.info("Delete existing generated file: " + file.canonicalPath)
                    file.delete()
                }
            }

        } catch (e: IOException) {
            log.error(e.message)
        }


    }*/

    override fun generation(orchaProgram: OrchaProgram) {

        val orchaMetadata = orchaProgram.orchaMetadata

        log.info("Generation of the output (Spring Integration) for the orcha program \"" + orchaMetadata.title + "\" begins.")

        /*val xmlSpringContextFileName = orchaMetadata.title!! + ".xml"
        val xmlSpringContent = sourceCodeDirectory.absolutePath + File.separator + "resources" + File.separator + xmlSpringContextFileName
        val xmlSpringContextFile = File(xmlSpringContent)

        val xmlSpringContextQoSFileName = orchaMetadata.title!! + "QoS.xml"
        val xmlQoSSpringContent = sourceCodeDirectory.absolutePath + File.separator + "resources" + File.separator + xmlSpringContextQoSFileName
        val xmlQoSSpringContextFile = File(xmlQoSSpringContent)*/

        val stringWriter = StringWriter()
        stringWriter.write("<beans xmlns=\"http://www.springframework.org/schema/beans\">")
        stringWriter.write("</beans>")
        stringWriter.flush()

        val s = stringWriter.toString()

        val inputStream = ByteArrayInputStream(s.toByteArray())

        val builder = SAXBuilder()

        val document = builder. build (inputStream)

        val graphOfInstructions = orchaProgram.integrationGraph

        this.transpile(document, orchaMetadata, graphOfInstructions)

        val pathToResources = "." + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + orchaMetadata.title + ".xml"
        val file = File(pathToResources)
        val xml = XMLOutputter()
        xml.format = Format.getPrettyFormat()
        val xmlProcessor = xml.xmlOutputProcessor
        val fw = FileWriter(file)
        xmlProcessor.process(fw, Format.getPrettyFormat(), document)
        fw.close()

        log.info("XML configuration file for Spring Integration has been generated: " + file.canonicalPath)
        log.info("Generation of the output (Spring Integration) for the orcha program \"" + orchaMetadata.title + "\" complete successfully.")
    }

    private fun transpile(document: Document, orchaMetadata: OrchaMetadata, graphOfInstructions: List<IntegrationNode>) {

        val rootElement = document.rootElement

        var channelName: String = "input"
        var channelNumber: Int = 1

        for (node in graphOfInstructions) {

            log.info("Generation of XML code for the node: " + node)
            log.info("Generation of XML code for the instruction: " + node.instruction!!.instruction)

            when(node.integrationPattern) {
                IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER -> {

                }
                IntegrationNode.IntegrationPattern.MESSAGE_FILTER -> {
                    when(node.instruction){
                        is ReceiveInstruction -> {
                            val receive: ReceiveInstruction = node.instruction as ReceiveInstruction
                            val element = filter(receive.condition)
                            element.setAttribute("input-channel", channelName)
                            channelName = "OutputFilter" + channelNumber
                            channelNumber++
                            element.setAttribute("output-channel", channelName)
                            rootElement.addContent(element)
                        }
                    }
                }
                IntegrationNode.IntegrationPattern.SERVICE_ACTIVATOR -> {
                    when(node.instruction){
                        is ComputeInstruction -> {

                        }
                    }
                }
                IntegrationNode.IntegrationPattern.AGGREGATOR -> {
                    when(node.instruction){
                        is WhenInstruction -> {
                            val whenInstruction: WhenInstruction = node.instruction as WhenInstruction
                            val element = aggregate(whenInstruction.aggregationExpression)
                            element.setAttribute("input-channel", channelName)
                            channelName = "OutputFilter" + channelNumber
                            channelNumber++
                            element.setAttribute("output-channel", channelName)
                            rootElement.addContent(element)
                        }
                    }
                }
                else -> {

                }
            }


            val adjacentNodes = node.nextIntegrationNodes

            this.transpile(document, orchaMetadata, adjacentNodes)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(OutputGenerationToSpringIntegration::class.java)
    }

}
