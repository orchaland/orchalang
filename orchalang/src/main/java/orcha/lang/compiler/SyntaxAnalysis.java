package orcha.lang.compiler;

public interface SyntaxAnalysis {

    OrchaProgram analysis(OrchaProgram orchaProgram) throws OrchaCompilationException;

}
