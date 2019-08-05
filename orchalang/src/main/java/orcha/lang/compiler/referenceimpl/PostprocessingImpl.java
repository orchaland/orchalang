package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.OrchaCompilationException;
import orcha.lang.compiler.OrchaProgram;
import orcha.lang.compiler.Postprocessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostprocessingImpl implements Postprocessing {

    private static Logger log = LoggerFactory.getLogger(PostprocessingImpl.class);

    @Override
    public OrchaProgram process(OrchaProgram orchaProgram) throws OrchaCompilationException {
        log.info("Post processing of the orcha program \"" + orchaProgram.getOrchaMetadata().getTitle() + "\" begins.");
        log.info("Post processing of the orcha program \"" + orchaProgram.getOrchaMetadata().getTitle() + "\" complete successfuly.");
        return orchaProgram;
    }
}
