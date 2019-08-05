package orcha.lang.compiler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

class OrchaCompiler {

    @Autowired
    internal var preprocessing: Preprocessing? = null

    @Autowired
    @Qualifier("lexicalAnalysisForOrchaCompiler")
    internal var lexicalAnalysis: LexicalAnalysis? = null

    @Autowired
    internal var syntaxAnalysis: SyntaxAnalysis? = null

    @Autowired
    internal var semanticAnalysis: SemanticAnalysis? = null

    @Autowired
    internal var postprocessing: Postprocessing? = null

    @Autowired
    internal var linkEditor: LinkEditor? = null

    @Autowired
    internal var outputGeneration: OutputGeneration? = null

    @Throws(OrchaCompilationException::class)
    fun compile(orchaFileName: String) {

        val linesOfCode = preprocessing!!.process(orchaFileName)

        var orchaProgram = lexicalAnalysis!!.analysis(linesOfCode)

        orchaProgram = syntaxAnalysis!!.analysis(orchaProgram)

        orchaProgram = semanticAnalysis!!.analysis(orchaProgram)

        orchaProgram = postprocessing!!.process(orchaProgram)

        orchaProgram = linkEditor!!.link(orchaProgram)

        outputGeneration!!.generation(orchaProgram)

    }

    companion object {
        private val log = LoggerFactory.getLogger(OrchaCompiler::class.java)
    }

}
