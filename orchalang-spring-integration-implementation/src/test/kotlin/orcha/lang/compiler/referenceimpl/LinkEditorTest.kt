package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.*
import orcha.lang.compiler.referenceimpl.springIntegration.Properties
import orcha.lang.compiler.syntax.*
import orcha.lang.configuration.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest
@ContextConfiguration(classes = [Properties::class, CompilerReferenceImplTestConfiguration::class])
class LinkEditorTest {

    @Autowired
    internal var semanticAnalysisForTest: SemanticAnalysis? = null

    @Autowired
    internal var linkEditorForTest: LinkEditor? = null

    @Test
    fun checkForConfiguration() {

        try {
            val titleInstruction: Instruction = TitleInstruction("title: check order")
            titleInstruction.lineNumber = 1
            titleInstruction.analysis()
            val orchaMetadata = OrchaMetadata()
            orchaMetadata.add(titleInstruction)


            var integrationNodes: MutableList<IntegrationNode> = ArrayList()

            val instruction1: Instruction = ReceiveInstruction("receive travelInfo from travelAgency")
            instruction1.lineNumber = 1
            instruction1.analysis()

            integrationNodes.add(IntegrationNode(instruction1))


            val instruction2: Instruction = ComputeInstruction("compute checkOrder with travelInfo")
            instruction2.lineNumber = 2
            instruction2.analysis()

            integrationNodes.add(IntegrationNode(instruction2))


            val instruction3: Instruction = WhenInstruction("when \"checkOrder terminates\"")
            instruction3.lineNumber = 3
            instruction3.analysis()

            integrationNodes.add(IntegrationNode(instruction3))


            val instruction4: Instruction = SendInstruction("send checkOrder to customer")
            instruction4.lineNumber = 4
            instruction4.analysis()

            integrationNodes.add(IntegrationNode(instruction4))

            var orchaProgram = OrchaProgram()
            orchaProgram.integrationGraph = integrationNodes
            orchaProgram.orchaMetadata = orchaMetadata

            orchaProgram = semanticAnalysisForTest!!.analysis(orchaProgram)

            orchaProgram = linkEditorForTest!!.link(orchaProgram)

            integrationNodes = orchaProgram.integrationGraph as MutableList<IntegrationNode>

            Assert.assertTrue(integrationNodes.size == 4)

            Assert.assertTrue(integrationNodes[3].instruction == instruction4)

            var channelAdapter = integrationNodes[0]
            Assert.assertNotNull(channelAdapter.instruction?.configuration)
            Assert.assertTrue(channelAdapter.instruction?.configuration is EventHandler)
            var eventHandler: EventHandler = channelAdapter.instruction?.configuration as EventHandler
            Assert.assertEquals(eventHandler.name, "travelAgency")

            val serviceActivator = integrationNodes[1]
            Assert.assertNotNull(serviceActivator.instruction?.configuration)
            Assert.assertTrue(serviceActivator.instruction?.configuration is Application)
            val application: Application = serviceActivator.instruction?.configuration as Application
            Assert.assertEquals(application.name, "checkOrder")

            channelAdapter = integrationNodes[3]
            Assert.assertNotNull(channelAdapter.instruction?.configuration)
            Assert.assertTrue(channelAdapter.instruction?.configuration is Map<*, *>)
            val configuration: MutableMap<String,EventHandler> = channelAdapter.instruction?.configuration as MutableMap<String,EventHandler>
            Assert.assertNotNull(configuration)
            eventHandler = configuration["customer"]!!
            Assert.assertEquals(eventHandler.name, "customer")

        } catch (e: OrchaCompilationException){
            Assert.fail(e.message)
        }


    }

}