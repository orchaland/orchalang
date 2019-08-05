package orcha.lang.compiler;

/**
 * Must be implemented into another project (the Spring Integration implementation of the Orcha compiler as instance)
 */
public interface OutputGeneration {

    public void generation(OrchaProgram orchaProgram);

}
