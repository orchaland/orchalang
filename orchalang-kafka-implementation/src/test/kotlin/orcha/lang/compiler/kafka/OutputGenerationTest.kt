package orcha.lang.compiler.kafka

import orcha.lang.compiler.*
import orcha.lang.compiler.referenceimpl.*
import orcha.lang.compiler.syntax.*
import orcha.lang.configuration.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [TestConfig::class])
@EnableConfigurationProperties(value = [Properties::class])
@ActiveProfiles("test")
class OutputGenerationTest {

    @Autowired
    internal var semanticAnalysisForKotlinTest: SemanticAnalysis? = null

    @Autowired
    internal var linkEditorForTest: LinkEditor? = null

    @Autowired
    internal var outputGenerationForTest: OutputGeneration? = null

    @Test
    fun kafkaCodeGeneration() {

        try {
            val titleInstruction: Instruction = TitleInstruction("title: check order")
            titleInstruction.lineNumber = 1
            titleInstruction.analysis()
            val orchaMetadata = OrchaMetadata()
            orchaMetadata.add(titleInstruction)
            val domainInstruction : DomainInstruction = DomainInstruction("domain: travel")
            domainInstruction.analysis()
            domainInstruction.lineNumber = 2
            orchaMetadata.add(domainInstruction)

            var integrationNodes: MutableList<IntegrationNode> = ArrayList()

            val instruction1: Instruction = ReceiveInstruction("receive travelInfo from travelAgency")
            instruction1.lineNumber = 3
            instruction1.analysis()

            integrationNodes.add(IntegrationNode(instruction1))


            val instruction2: Instruction = ComputeInstruction("compute checkOrder with travelInfo")
            instruction2.lineNumber = 4
            instruction2.analysis()

            integrationNodes.add(IntegrationNode(instruction2))


            val instruction3: Instruction = WhenInstruction("when \"checkOrder terminates\"")
            instruction3.lineNumber = 5
            instruction3.analysis()

            integrationNodes.add(IntegrationNode(instruction3))


            val instruction4: Instruction = SendInstruction("send checkOrder to customer")
            instruction4.lineNumber = 6
            instruction4.analysis()

            integrationNodes.add(IntegrationNode(instruction4))

            var orchaProgram = OrchaProgram()
            orchaProgram.integrationGraph = integrationNodes
            orchaProgram.orchaMetadata = orchaMetadata

            orchaProgram = semanticAnalysisForKotlinTest!!.analysis(orchaProgram)

            orchaProgram = linkEditorForTest!!.link(orchaProgram)

            val generatedCode = outputGenerationForTest!!.generation(orchaProgram)

        } catch (e: OrchaCompilationException){
            Assert.fail(e.message)
        }


    }

}