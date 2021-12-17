package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.*
import orcha.lang.compiler.referenceimpl.springIntegration.*
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
@ContextConfiguration(classes = [LexicalAnalysisTest.CompilerReferenceImplTestConfiguration::class])
class LexicalAnalysisTest {

    @TestConfiguration
    internal class CompilerReferenceImplTestConfiguration {

        @Bean
        @DependsOn("whenInstruction", "sendInstruction")
        internal fun lexicalAnalysisForTest(): LexicalAnalysis {
            return LexicalAnalysisImpl()
        }

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

    }


    @Autowired
    internal var lexicalAnalysisForTest: LexicalAnalysis? = null

    @Test
    fun test() {

        try {
            Assert.assertTrue(lexicalAnalysisForTest != null)

            val linesOfCode = ArrayList(
                    Arrays.asList(
                            "title: title",
                            "",
                            "receive order from customer",
                            "when \"order terminates\"",
                            "send order.number to customer"))

            val orchaProgram = lexicalAnalysisForTest!!.analysis(linesOfCode)

            Assert.assertNotNull(orchaProgram)
            Assert.assertNotNull(orchaProgram.integrationGraph)
            Assert.assertNotNull(orchaProgram.orchaMetadata)

            val orchaMetadata = orchaProgram.orchaMetadata
            val metadata = orchaMetadata!!.metadata
            Assert.assertNotNull(metadata)
            Assert.assertEquals(metadata.size.toLong(), 1)
            val instruction = metadata[0]
            Assert.assertTrue(instruction is TitleInstruction)


            val graphOfInstructions = orchaProgram.integrationGraph

            Assert.assertTrue(graphOfInstructions!!.size == 3)

            var integrationNode = graphOfInstructions!![1]
            Assert.assertNotNull(integrationNode)
            var inst = integrationNode.instruction
            if (inst != null) {
                inst.analysis()
            }
            Assert.assertNotNull(inst)
            Assert.assertTrue(inst is WhenInstructionForSpringIntegration)
            val whenInstruction = inst as WhenInstructionForSpringIntegration
            Assert.assertEquals(whenInstruction.getLineNumber(), 4)
            Assert.assertTrue(whenInstruction.getCommand().equals("when"))

            integrationNode = graphOfInstructions[2]
            Assert.assertNotNull(integrationNode)
            inst = integrationNode.instruction
            Assert.assertNotNull(inst)
            inst?.analysis()
            Assert.assertTrue(inst is SendInstructionForSpringIntegration)
            val sendInstruction = inst as SendInstructionForSpringIntegration
            Assert.assertEquals(sendInstruction.getLineNumber(), 5)
            Assert.assertTrue(sendInstruction.getCommand().equals("send"))
            Assert.assertEquals(sendInstruction.getVariables(), "payload.number")

        } catch (e: OrchaCompilationException) {
            Assert.fail(e.message)
        }

    }

}
