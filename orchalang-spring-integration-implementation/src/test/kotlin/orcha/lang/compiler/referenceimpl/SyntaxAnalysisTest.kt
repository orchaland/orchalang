package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.*
import orcha.lang.compiler.referenceimpl.springIntegration.OutputCodeGeneratorFactory
import orcha.lang.compiler.referenceimpl.springIntegration.SendInstructionFactory
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionFactory
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionForSpringIntegration
import orcha.lang.compiler.syntax.SendInstruction
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

@RunWith(SpringRunner::class)
//@SpringBootTest
@ContextConfiguration(classes = [SyntaxAnalysisTest.CompilerReferenceImplTestConfiguration::class])
class SyntaxAnalysisTest {

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

        @Bean
        internal fun syntaxAnalysisForTest(): SyntaxAnalysis {
            return SyntaxAnalysisImpl()
        }

    }

    @Autowired
    internal var syntaxAnalysisForTest: SyntaxAnalysis? = null

    /*@Resource(name = "&whenInstruction")
    WhenInstructionFactory whenInstructionFactory;*/

    @Test
    fun `when`() {

        var expression: String? = null

        try {

            expression = "	 when \"(event receives) and (one	 terminates  condition 	==false)\""
            var whenExpression: WhenInstruction = WhenInstructionForSpringIntegration(expression)
            //WhenInstruction whenExpression = whenInstructionFactory.getObject();
            //whenExpression.setInstruction(expression);
            whenExpression.analysis()
            var applicationOrEvents: List<WhenInstruction.ApplicationOrEventInExpression> = whenExpression.applicationsOrEvents
            Assert.assertNotNull(applicationOrEvents)
            Assert.assertEquals(applicationOrEvents.size.toLong(), 2)

            var applicationOrEventInExpression: WhenInstruction.ApplicationOrEventInExpression = applicationOrEvents[0]
            var name = applicationOrEventInExpression.name
            Assert.assertNotNull(name)
            Assert.assertEquals(name, "event")
            var state: WhenInstruction.State = applicationOrEventInExpression.state
            Assert.assertNotNull(state)
            Assert.assertEquals(state, WhenInstruction.State.RECEIVES)
            var order = applicationOrEventInExpression.order
            Assert.assertEquals(order.toLong(), 1)
            var condition = applicationOrEventInExpression.condition
            Assert.assertNull(condition)

            applicationOrEventInExpression = applicationOrEvents[1]
            name = applicationOrEventInExpression.name
            Assert.assertNotNull(name)
            Assert.assertEquals(name, "one")
            state = applicationOrEventInExpression.state
            Assert.assertNotNull(state)
            Assert.assertEquals(state, WhenInstruction.State.TERMINATES)
            order = applicationOrEventInExpression.order
            Assert.assertEquals(order.toLong(), 2)
            condition = applicationOrEventInExpression.condition
            Assert.assertNotNull(condition)
            Assert.assertEquals(condition, "==false")

            var aggregationExpression = whenExpression.aggregationExpression
            Assert.assertNotNull(aggregationExpression)
            Assert.assertEquals(aggregationExpression, "size()==2 AND ((((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.EventHandler))) AND (((getMessages().toArray())[1].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[1].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)))")


            expression = "when \"(one	 terminates  condition 	==false)\""
            whenExpression = WhenInstruction(expression)
            whenExpression.analysis()
            applicationOrEvents = whenExpression.applicationsOrEvents
            Assert.assertNotNull(applicationOrEvents)
            Assert.assertEquals(applicationOrEvents.size.toLong(), 1)

            applicationOrEventInExpression = applicationOrEvents[0]
            name = applicationOrEventInExpression.name
            Assert.assertNotNull(name)
            Assert.assertEquals(name, "one")
            state = applicationOrEventInExpression.state
            Assert.assertNotNull(state)
            Assert.assertEquals(state, WhenInstruction.State.TERMINATES)
            order = applicationOrEventInExpression.order
            Assert.assertEquals(order.toLong(), 1)
            condition = applicationOrEventInExpression.condition
            Assert.assertNotNull(condition)
            Assert.assertEquals(condition, "==false")



            expression = "when \"one	 terminates  condition 	==true\""
            whenExpression = WhenInstruction(expression)
            whenExpression.analysis()
            applicationOrEvents = whenExpression.applicationsOrEvents
            Assert.assertNotNull(applicationOrEvents)
            Assert.assertEquals(applicationOrEvents.size.toLong(), 1)

            applicationOrEventInExpression = applicationOrEvents[0]
            name = applicationOrEventInExpression.name
            Assert.assertNotNull(name)
            Assert.assertEquals(name, "one")
            state = applicationOrEventInExpression.state
            Assert.assertNotNull(state)
            Assert.assertEquals(state, WhenInstruction.State.TERMINATES)
            order = applicationOrEventInExpression.order
            Assert.assertEquals(order.toLong(), 1)
            condition = applicationOrEventInExpression.condition
            Assert.assertNotNull(condition)
            Assert.assertEquals(condition, "==true")



            expression = " when  \"  one	fails   \""
            whenExpression = WhenInstruction(expression)
            whenExpression.analysis()
            applicationOrEvents = whenExpression.applicationsOrEvents
            Assert.assertNotNull(applicationOrEvents)
            Assert.assertEquals(applicationOrEvents.size.toLong(), 1)

            applicationOrEventInExpression = applicationOrEvents[0]
            name = applicationOrEventInExpression.name
            Assert.assertNotNull(name)
            Assert.assertEquals(name, "one")
            state = applicationOrEventInExpression.state
            Assert.assertNotNull(state)
            Assert.assertEquals(state, WhenInstruction.State.FAILS)



            expression = "when \" ( ( one	 terminates  condition 	==false ) and ( two terminates condition ==true ) ) or ( three terminates ) \""
            whenExpression = WhenInstructionForSpringIntegration(expression)
            //whenExpression = whenInstructionFactory.getObject();
            //whenExpression.setInstruction(expression);
            whenExpression.analysis()
            aggregationExpression = whenExpression.aggregationExpression
            Assert.assertNotNull(aggregationExpression)
            Assert.assertEquals(aggregationExpression, "size()==3 AND (((((getMessages().toArray())[0].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)) AND (((getMessages().toArray())[1].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[1].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))) OR (((getMessages().toArray())[2].payload instanceof Transpiler(orcha.lang.App) AND (getMessages().toArray())[2].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)))")

        } catch (e: Exception) {
            Assert.fail("Syntax error in: " + expression!!)
        }

    }


}

