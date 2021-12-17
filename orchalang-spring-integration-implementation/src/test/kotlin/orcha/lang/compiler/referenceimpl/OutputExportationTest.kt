package orcha.lang.compiler.referenceimpl

import com.helger.jcodemodel.JCodeModel
import com.helger.jcodemodel.JDefinedClass
import orcha.lang.compiler.*
import orcha.lang.compiler.referenceimpl.springIntegration.OutputCodeGeneratorFactory
import orcha.lang.compiler.referenceimpl.springIntegration.SendInstructionFactory
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionFactory
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
import org.springframework.context.annotation.DependsOn
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [OutputExportationTest.CompilerReferenceImplTestConfiguration::class, Properties::class])
//@SpringBootTest(classes = [OutputExportationTest.CompilerReferenceImplTestConfiguration::class])
//@EnableConfigurationProperties(value = [Properties::class])
//@ContextConfiguration(classes = [OutputExportationTest.CompilerReferenceImplTestConfiguration::class, Properties::class])
//@ActiveProfiles("test")
class OutputExportationTest {

    @TestConfiguration
    internal class CompilerReferenceImplTestConfiguration {

        @Autowired
        internal var properties: Properties? = null

        //@Value("\${orcha.pathToBinaryCode:build/resources/main}")
        //var pathToBinaryCode: String? = null

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

        @Bean
        internal fun semanticAnalysisForTest(): SemanticAnalysis {
            return SemanticAnalysisImpl()
        }

        @Bean
        fun linkEditorForTest(): LinkEditor? {
            return LinkEditorImpl()
        }

        @Bean
        fun outputGenerationForTest() : OutputGeneration? {
            return OutputGenerationImpl()
        }

        @Bean(name = ["outputCodeGenerator"])
        fun outputCodeGeneratorFactory(): OutputCodeGeneratorFactory {
            return OutputCodeGeneratorFactory()
        }

        //@ConditionalOnMissingBean
        //@DependsOn("outputCodeGenerator")
        @Bean
        fun outputExportationForTest(): OutputExportation? {
            return OutputExportationImpl()
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
            val databaseAdapter = DatabaseAdapter()
            eventHandler.output = Output(databaseAdapter, "application/java-archive")
            return eventHandler
        }


    }


    @Autowired
    internal var semanticAnalysisForTest: SemanticAnalysis? = null

    @Autowired
    internal var linkEditorForTest: LinkEditor? = null

    @Autowired
    internal var outputGenerationForTest: OutputGeneration? = null

    @Autowired
    internal var outputExportationForTest: OutputExportation? = null

    @Test
    fun springIntegrationCodeExportation() {

        try {
            val titleInstruction: Instruction = TitleInstruction("title: check order")
            titleInstruction.lineNumber = 1
            titleInstruction.analysis()
            val orchaMetadata = OrchaMetadata()
            orchaMetadata.add(titleInstruction)
            val domainInstruction: DomainInstruction = DomainInstruction("domain: travel")
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

            orchaProgram = semanticAnalysisForTest!!.analysis(orchaProgram)

            orchaProgram = linkEditorForTest!!.link(orchaProgram)

            val codeGeneratedModel = outputGenerationForTest!!.generation(orchaProgram)

            outputExportationForTest!!.export(codeGeneratedModel)

        } catch (e: OrchaCompilationException) {
            Assert.fail(e.message)
        }

    }
}