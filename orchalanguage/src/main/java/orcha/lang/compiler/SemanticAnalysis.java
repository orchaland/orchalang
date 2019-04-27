package orcha.lang.compiler;

public interface SemanticAnalysis {

    OrchaProgram analysis(OrchaProgram orchaSmartContract) throws OrchaCompilationException;

}
