package orcha.lang.compiler

import org.slf4j.LoggerFactory

class OrchaCompiler(private val preprocessing: Preprocessing, private val lexicalAnalysis: LexicalAnalysis, private val syntaxAnalysis: SyntaxAnalysis, private val semanticAnalysis: SemanticAnalysis, private val postprocessing: Postprocessing, private val linkEditor: LinkEditor, private val outputGeneration: OutputGeneration, private val outputExportation: OutputExportation) {

    @Throws(OrchaCompilationException::class)
    fun compile(orchaFileName: String) {

        val linesOfCode = preprocessing!!.process(orchaFileName)

        var orchaProgram = lexicalAnalysis!!.analysis(linesOfCode)

        orchaProgram = syntaxAnalysis!!.analysis(orchaProgram)

        orchaProgram = semanticAnalysis!!.analysis(orchaProgram)

        orchaProgram = postprocessing!!.process(orchaProgram)

        orchaProgram = linkEditor!!.link(orchaProgram)

        val generatedCode = outputGeneration!!.generation(orchaProgram)

        outputExportation.export(generatedCode)

    }

    companion object {
        private val log = LoggerFactory.getLogger(OrchaCompiler::class.java)
    }

}
