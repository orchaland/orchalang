package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.*
import orcha.lang.compiler.syntax.*
import orcha.lang.configuration.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [LinkEditorTest.TestConfig::class])
@EnableConfigurationProperties(value = [Properties::class])
//@ActiveProfiles("test")
//@ContextConfiguration(classes = [Properties::class, CompilerReferenceImplKotlinTestConfiguration::class])
class LinkEditorTest {

    /*@Configuration
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "orchalang")
    @Profile("spring-intregation")
    internal class Properties {
        lateinit var pathToIntegrationGraph: String
    }*/

    @TestConfiguration
    internal class TestConfig {

        @Autowired
        private lateinit var properties: Properties

        /*@Bean(name = ["whenInstruction"])
        fun whenInstructionFactory(): WhenInstructionFactory {
            return WhenInstructionFactory()
        }

        @Bean
        @Throws(Exception::class)
        fun whenInstruction(): WhenInstruction? {
            return whenInstructionFactory().getObject()
        }*/

        /*@Bean(name = ["sendInstruction"])
        fun sendInstructionFactory(): SendInstructionFactory {
            return SendInstructionFactory()
        }

        @Bean
        @Throws(Exception::class)
        fun sendInstruction(): SendInstruction? {
            return sendInstructionFactory().getObject()
        }*/

        @Value("\${orcha.pathToBinaryCode:build/resources/main}")
        var pathToBinaryCode: String? = null

        /*@Bean
        fun preprocessingForTest(): Preprocessing? {
            return PreprocessingImpl()
        }*/

        /*@Bean
        @DependsOn("whenInstruction", "sendInstruction")
        fun lexicalAnalysisForTest(): LexicalAnalysis? {
            return LexicalAnalysisImpl()
        }*/

        /*@Bean
        fun syntaxAnalysisForTest(): SyntaxAnalysis? {
            return SyntaxAnalysisImpl()
        }*/

        @Bean
        fun semanticAnalysisForKotlinTest(): SemanticAnalysis? {
            return SemanticAnalysisImpl()
        }

        @Bean
        fun linkEditorForTest(): LinkEditor? {
            return LinkEditorImpl()
        }

        @Bean
        fun travelAgency(): EventHandler {
            val eventHandler = EventHandler("travelAgency")
            val fileAdapter = InputFileAdapter("./files", "*.json")
            eventHandler.input = Input(fileAdapter, "java.lang.String")
            return eventHandler;
        }

        @Bean
        fun checkOrder(): Application {
            val application = Application("checkOrder", "Kotlin")
            val javaAdapter = JavaServiceAdapter("orcha.lang.compiler.referenceimpl.PreprocessingImpl", "process")
            application.input = Input(javaAdapter, "java.lang.String")
            application.output = Output(javaAdapter, "java.util.List<java.lang.String>")
            return application
        }

        @Bean
        fun customer(): EventHandler {
            val eventHandler = EventHandler("customer")
            val fileAdapter = OutputFileAdapter("data/output", "orchaCompiler.java")
            eventHandler.output = Output(fileAdapter, "application/java-archive")
            return eventHandler
        }


    }

    @Autowired
    internal var semanticAnalysisForKotlinTest: SemanticAnalysis? = null

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

            orchaProgram = semanticAnalysisForKotlinTest!!.analysis(orchaProgram)

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