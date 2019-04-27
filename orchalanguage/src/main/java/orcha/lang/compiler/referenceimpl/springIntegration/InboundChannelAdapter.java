package orcha.lang.compiler.referenceimpl.springIntegration;

import orcha.lang.compiler.IntegrationNode;
import orcha.lang.compiler.OrchaMetadata;

public interface InboundChannelAdapter {

    void transpile(OrchaMetadata orchaMetadata, IntegrationNode instructionNode);

}
