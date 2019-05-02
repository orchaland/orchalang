package orcha.lang.compiler.referenceimpl.springIntegration

import orcha.lang.compiler.IntegrationNode
import orcha.lang.compiler.OrchaMetadata
import orcha.lang.compiler.OrchaProgram
import orcha.lang.compiler.OutputGeneration
import org.jdom2.Document
import org.jdom2.input.SAXBuilder
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.StringWriter

//class OutputGenerationToSpringIntegration(pathToBinaryCode: String, internal var binaryCodeDirectory: File) : OutputGeneration {
class OutputGenerationToSpringIntegration : OutputGeneration {

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

    override fun generation(orchaProgram: OrchaProgram): Any? {

        val orchaMetadata = orchaProgram.orchaMetadata

        log.info("Generation of the output for the orcha program \"" + orchaMetadata.title + "\" begins.")

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

        log.info("Generation of the output for the orcha program \"" + orchaMetadata.title + "\" complete successfully.")

        return document
    }

    private fun transpile(document: Document, orchaMetadata: OrchaMetadata, graphOfInstructions: List<IntegrationNode>) {

        for (node in graphOfInstructions) {

            when (node.instruction.command) {
                "receive" -> {
                }
                "compute" -> {
                }
                "when" -> {
                }
                "send" -> {
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
