package orcha.lang.compiler;

import orcha.lang.compiler.syntax.Instruction;

import java.util.ArrayList;
import java.util.List;

public class IntegrationNode {

    public enum IntegrationPattern{
        CHANNEL_ADAPTER,
        MESSAGE_FILTER,
        MESSAGE_TRANSLATOR,
        MESSAGE_ROUTER,
        SERVICE_ACTIVATOR,
        AGGREGATOR,
        RESEQUENCER;
    }

    IntegrationPattern integrationPattern;

    Instruction instruction;

    List<IntegrationNode> nextIntegrationNodes = new ArrayList<IntegrationNode>();

    public IntegrationNode(){
    }

    public IntegrationNode(Instruction instruction) {
        this.instruction = instruction;
    }

    public IntegrationPattern getIntegrationPattern() {
        return integrationPattern;
    }

    public void setIntegrationPattern(IntegrationPattern integrationPattern) {
        this.integrationPattern = integrationPattern;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public List<IntegrationNode> getNextIntegrationNodes() {
        return nextIntegrationNodes;
    }

    public void setNextIntegrationNodes(List<IntegrationNode> nextIntegrationNodes) {
        this.nextIntegrationNodes = nextIntegrationNodes;
    }
}
