package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.*
import orcha.lang.compiler.referenceimpl.springIntegration.OutputCodeGeneratorFactory
import orcha.lang.compiler.referenceimpl.springIntegration.SendInstructionFactory
import orcha.lang.compiler.referenceimpl.springIntegration.SendInstructionForSpringIntegration
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionFactory
import orcha.lang.compiler.syntax.Instruction
import orcha.lang.compiler.syntax.SendInstruction
import orcha.lang.compiler.syntax.TitleInstruction
import orcha.lang.compiler.syntax.WhenInstruction
import orcha.lang.configuration.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
//@SpringBootTest
@ContextConfiguration(classes = [SemanticAnalysisTest.CompilerReferenceImplTestConfiguration::class])
class SemanticAnalysisTest {

    @TestConfiguration
    internal class CompilerReferenceImplTestConfiguration {

        @Bean(name = ["whenInstruction"])
        fun whenInstructionFactory(): WhenInstructionFactory {
            return WhenInstructionFactory()
        }

        @Bean
        @Throws(Exception::class)
        fun whenInstruction(): WhenInstruction? {
            return whenInstructionFactory().getObject()
        }

        @Bean(name = ["sendInstruction"])
        fun sendInstructionFactory(): SendInstructionFactory {
            return SendInstructionFactory()
        }

        @Bean
        @Throws(Exception::class)
        fun sendInstruction(): SendInstruction? {
            return sendInstructionFactory().getObject()
        }

        @Bean
        internal fun semanticAnalysisForTest(): SemanticAnalysis {
            return SemanticAnalysisImpl()
        }

    }

    @Autowired
    internal var semanticAnalysisForTest: SemanticAnalysis? = null

    @Test
    fun sendInstructionWithResultAsVariable() {
        try {
            val titleInstruction: Instruction = TitleInstruction("title: check data")
            titleInstruction.lineNumber = 1

            titleInstruction.analysis()

            val orchaMetadata = OrchaMetadata()
            orchaMetadata.add(titleInstruction)

            var integrationNodes: MutableList<IntegrationNode> = ArrayList()

            val instruction1: Instruction = SendInstructionForSpringIntegration("send data.result to output")
            instruction1.lineNumber = 1

            instruction1.analysis()

            integrationNodes.add(IntegrationNode(instruction1))

            var orchaProgram = OrchaProgram()
            orchaProgram.integrationGraph = integrationNodes
            orchaProgram.orchaMetadata = orchaMetadata
            orchaProgram = semanticAnalysisForTest!!.analysis(orchaProgram)

            integrationNodes = orchaProgram.integrationGraph as MutableList<IntegrationNode>

            Assert.assertTrue(integrationNodes.size == 1)
            val adapter = integrationNodes[0]
            Assert.assertTrue(adapter.instruction == instruction1)

            Assert.assertTrue(adapter.instruction is SendInstructionForSpringIntegration)

            val si = adapter.instruction as SendInstructionForSpringIntegration?
            Assert.assertEquals(si!!.variables, "payload.result")

            Assert.assertEquals(adapter.integrationPattern, IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER)

            val nextNodes = adapter.nextIntegrationNodes
            Assert.assertNotNull(nextNodes)
            Assert.assertEquals(nextNodes.size.toLong(), 0)

        } catch (e: OrchaCompilationException) {
            Assert.fail(e.message)
        }
    }
}