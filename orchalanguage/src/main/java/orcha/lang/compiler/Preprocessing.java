package orcha.lang.compiler;

import java.util.List;

public interface Preprocessing {

    /**
     *
     * Add compute (if not present) before each service call.
     *
     * Example of an Orcha program:
     *
     * receive order from customer
     * checkOrder with order.value
     * ...
     *
     * compute is added before checkOrder, and the instruction becomes:
     *
     * compute checkOrder with order.value
     *
     * @see <a href="http://www.orchalang.com/orchaSpecifications1-0.htm#language">Orcha syntax</a>
     *
     * @param orchaFileName An orcha source file (.orcha extension) should be in .src/main/orcha/source.
     * If the orcha file is in a subdirectory of .src/main/orcha/source, add this subdirectory to the command line like directoryName/orchaFileName.orcha
     * @return
     */
    List<String> process(String orchaFileName) throws OrchaCompilationException;

}
