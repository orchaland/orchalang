package orcha.lang.compiler;

public interface Postprocessing {

    OrchaProgram process(OrchaProgram orchaProgram) throws OrchaCompilationException;

}
