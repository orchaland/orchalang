package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.OrchaCompilationException;
import orcha.lang.compiler.OrchaProgram;
import orcha.lang.compiler.Postprocessing;

public class PostprocessingImpl implements Postprocessing {
    @Override
    public OrchaProgram process(OrchaProgram orchaProgram) throws OrchaCompilationException {
        return orchaProgram;
    }
}
