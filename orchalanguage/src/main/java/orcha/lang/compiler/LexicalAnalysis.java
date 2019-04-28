package orcha.lang.compiler;

import java.util.List;

public interface LexicalAnalysis {

    OrchaProgram analysis(List<String> linesOfCode) throws OrchaCompilationException;

}
