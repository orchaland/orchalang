package orcha.lang.compiler;

import java.util.List;

public class OrchaProgram {

    List<IntegrationNode> integrationGraph;
    OrchaMetadata orchaMetadata;

    public OrchaProgram(){
    }

    public OrchaProgram(List<IntegrationNode> integrationGraph, OrchaMetadata orchaMetadata) {
        this.integrationGraph = integrationGraph;
        this.orchaMetadata = orchaMetadata;
    }

    public List<IntegrationNode> getIntegrationGraph() {
        return integrationGraph;
    }

    public void setIntegrationGraph(List<IntegrationNode> integrationGraph) {
        this.integrationGraph = integrationGraph;
    }

    public OrchaMetadata getOrchaMetadata() {
        return orchaMetadata;
    }

    public void setOrchaMetadata(OrchaMetadata orchaMetadata) {
        this.orchaMetadata = orchaMetadata;
    }
}
