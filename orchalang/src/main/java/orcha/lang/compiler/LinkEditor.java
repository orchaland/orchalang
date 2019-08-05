package orcha.lang.compiler;

/**
 * Must be implemented into another project (the Spring Integration implementation of the Orcha compiler as instance)
 */

public interface LinkEditor {

    OrchaProgram link(OrchaProgram orchaProgram) throws OrchaCompilationException;

}
