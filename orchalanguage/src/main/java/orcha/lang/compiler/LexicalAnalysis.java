package orcha.lang.compiler;

public interface LexicalAnalysis {

    OrchaProgram analysis(String orchaFileName) throws OrchaCompilationException;

}
