package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SyntaxAnalysisImpl implements SyntaxAnalysis {

    private static Logger log = LoggerFactory.getLogger(SyntaxAnalysisImpl.class);

    @Override
    public OrchaProgram analysis(OrchaProgram orchaProgram) throws OrchaCompilationException {

        log.info("Syntax analysis of the orcha program:" + orchaProgram.getOrchaMetadata().getTitle() + " begins.");

        this.metaDataAnalysis(orchaProgram.getOrchaMetadata());
        this.instructionAnalysis(orchaProgram.getIntegrationGraph());

        log.info("Syntax analysis of the orcha program:" + orchaProgram.getOrchaMetadata().getTitle() + " complete successfuly.");

        return orchaProgram;

    }

    private void metaDataAnalysis(OrchaMetadata orchaMetadata) {
    }

    private void instructionAnalysis(List<IntegrationNode> orchaMetadata) throws OrchaCompilationException {
        for (IntegrationNode node : orchaMetadata) {
            node.getInstruction().analysis();
        }
    }
}
