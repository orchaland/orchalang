package orcha.lang.compiler;

public interface SemanticAnalysis {

    OrchaProgram analysis(OrchaProgram orchaProgram) throws OrchaCompilationException;

}
