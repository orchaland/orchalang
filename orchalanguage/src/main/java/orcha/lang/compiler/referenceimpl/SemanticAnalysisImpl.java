package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SemanticAnalysisImpl implements SemanticAnalysis {

    private static Logger log = LoggerFactory.getLogger(SemanticAnalysisImpl.class);

    @Override
    public OrchaProgram analysis(OrchaProgram orchaProgram) throws OrchaCompilationException {

        boolean exception = false;

        // The complete list of instructions in the orcha program
        List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

        // Group instructions by receive instruction, compute, when and sendf
        Map<String, List<Instruction>> instructionsByCommand = instructions.stream()
                .collect(Collectors.groupingBy(Instruction::getCommand));

        List<Instruction> receives = instructionsByCommand.get("receive");
        exception = this.receiveAnalysis(receives, orchaProgram, exception);

        List<Instruction> computes = instructionsByCommand.get("compute");
        exception = this.computeAnalysis(computes, orchaProgram, exception);

        List<Instruction> whens = instructionsByCommand.get("when");
        exception = this.whenAnalysis(whens, orchaProgram, exception);

        List<Instruction> sends = instructionsByCommand.get("send");
        this.sendAnalysis(sends, orchaProgram, exception);

        if (exception == true) {
            throw new OrchaCompilationException("Semantic analysis error.");
        }

        return orchaProgram;
    }

    private boolean receiveAnalysis(List<Instruction> receives, OrchaProgram orchaProgram, boolean exception) {

        if (receives != null) {

            List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

            for (Instruction r : receives) {

                ReceiveInstruction receive = (ReceiveInstruction) r;

                // search for the instruction right after the receive
                Instruction afterReceive = instructions.stream().filter(instruction -> instruction.getLineNumber() > receive.getLineNumber()).findFirst().orElse(null);

                if (afterReceive == null) {
                    exception = true;
                    String message = "receive instruction should be followed by a when, a compute, or a send instruction";
                    log.error("Error at line " + receive.getLineNumber() + " for the instruction (" + receive.getInstruction() + ") cause by: " + message, new OrchaCompilationException("receive instruction should be followed by a when, a compute, or a send instruction", afterReceive.getLineNumber(), afterReceive.getInstruction()));
                }

                switch (afterReceive.getCommand()) {
                    case "compute":
                        if (((ComputeInstruction) afterReceive).getParameters().contains(receive.getEvent()) == false) {
                            exception = true;
                            String message = "compute instruction following a receive does not contain a with as excepted: with should contain " + receive.getEvent();
                            log.error("Error at line " + afterReceive.getLineNumber() + " for the instruction (" + afterReceive.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction()));
                        }
                        break;
                    case "send":
                        if (((SendInstruction) afterReceive).getData().equals(receive.getEvent()) == false) {
                            exception = true;
                            String message = "send instruction does not contain the received event: " + receive.getEvent() + ".";
                            log.error("Error at line " + afterReceive.getLineNumber() + " for the instruction (" + afterReceive.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction()));
                        }
                        break;
                    case "when":
                        WhenInstruction whenInstruction = (WhenInstruction) afterReceive;
                        WhenInstruction.ApplicationOrEventInExpression applicationOrEventInExpression = whenInstruction.getApplicationsOrEvents().stream().filter(event -> event.getName().equals(receive.getEvent())).findAny().orElse(null);
                        if (applicationOrEventInExpression == null) {
                            exception = true;
                            String message = "when instruction following a receive does not contain the received event in its condition : " + receive.getEvent() + ".";
                            log.error("Error at line " + whenInstruction.getLineNumber() + " for the instruction (" + whenInstruction.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction()));
                        }
                        break;
                    default:
                        exception = true;
                        String message = "receive instruction should be followed by a when, a compute, or a send instruction";
                        log.error("Error at line " + receive.getLineNumber() + " for the instruction (" + receive.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction()));
                }

                // search for the instruction node containing the receive instruction => then add a node has an adjacent node
                IntegrationNode receiveNode = orchaProgram.getIntegrationGraph().stream().filter(instructionNode -> instructionNode.getInstruction() == receive).findFirst().orElse(null);
                receiveNode.getNextIntegrationNodes().add(new IntegrationNode(afterReceive));

            }

            // look for all the receive instructions on the same event: multiple lines with "receive event ..."
            Map<String, List<Instruction>> receivesWithTheSameEvent = instructions.stream()
                    .filter(instruction -> instruction.getCommand().equals("receive"))
                    .collect(Collectors.groupingBy(instruction -> ((ReceiveInstruction) instruction).getEvent()));

            Set<String> events = receivesWithTheSameEvent.keySet();
            for (String event : events) {

                // for all the receive instructions on the same event: add them as adjacent nodes to a fictitious receive nodes
                List<Instruction> receiveInstructions = receivesWithTheSameEvent.get(event);

                // where is the receive instruction into the list of nodes ?
                List<IntegrationNode> nodes = orchaProgram.getIntegrationGraph();
                int index = IntStream.range(0, nodes.size())
                        .filter(i -> nodes.get(i).getInstruction().equals(receiveInstructions.get(0)))
                        .findAny()
                        .getAsInt();

                if (receiveInstructions.size() > 1) {     // there are many receive instructions with the same event

                    // all the receive instructions should have the same source also
                    ReceiveInstruction firstReceive = (ReceiveInstruction) receiveInstructions.get(0);
                    String source = firstReceive.getSource();
                    boolean sameSource = receiveInstructions.stream()
                            .allMatch(receive -> ((ReceiveInstruction) receive).getSource().equals(source));

                    if (sameSource == false) {    // not all the receive instructions have the same event
                        exception = true;
                        String message = "all the receive instructions having " + event + " as event should have the same source also.";
                        log.error(message, new OrchaCompilationException(message));
                    }

                    ReceiveInstruction fictitiousReceive = new ReceiveInstruction();
                    fictitiousReceive.setEvent(event);
                    fictitiousReceive.setSource(source);

                    // a channel adapter comes first (receive instruction)
                    IntegrationNode adapterNode = new IntegrationNode(fictitiousReceive);
                    adapterNode.setIntegrationPattern(IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER);

                    // do the receive instructions have the same condition
                    boolean sameCondition = false;
                    String condition = firstReceive.getCondition();
                    if (condition != null) {
                        sameCondition = receiveInstructions.stream()
                                .allMatch(receive -> ((ReceiveInstruction) receive).getCondition().equals(condition));
                    }

                    IntegrationNode routerNode = new IntegrationNode(fictitiousReceive);
                    routerNode.setIntegrationPattern(IntegrationNode.IntegrationPattern.MESSAGE_ROUTER);

                    // yes, the receive instructions have the same condition
                    if (sameCondition == true) {

                        // insert a message filter between the adapter and the router
                        fictitiousReceive.setCondition(condition);
                        IntegrationNode filterNode = new IntegrationNode(fictitiousReceive);
                        filterNode.setIntegrationPattern(IntegrationNode.IntegrationPattern.MESSAGE_FILTER);
                        adapterNode.getNextIntegrationNodes().add(filterNode);
                        filterNode.getNextIntegrationNodes().add(routerNode);
                    } else {    // there is no need of a filter since the receive instructions do not have the same conditon
                        // the channel adapter is followed by a router
                        adapterNode.getNextIntegrationNodes().add(routerNode);
                    }

                    // then the router leads to the receives instructions
                    for (Instruction receiveInstruction : receiveInstructions) {
                        routerNode.getNextIntegrationNodes().add(new IntegrationNode(receiveInstruction));
                    }

                    // finally add the adapterNode at the right place
                    nodes.add(index, adapterNode);

                } else {        // there is a single receive instruction with the event

                    IntegrationNode receiveNode = nodes.get(index);
                    receiveNode.setIntegrationPattern(IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER);

                    if (((ReceiveInstruction) receiveNode.getInstruction()).getCondition() != null) {
                        // insert a message filter
                        IntegrationNode filterNode = new IntegrationNode();
                        filterNode.setIntegrationPattern(IntegrationNode.IntegrationPattern.MESSAGE_FILTER);
                        filterNode.getNextIntegrationNodes().add(receiveNode.getNextIntegrationNodes().get(0));
                        receiveNode.getNextIntegrationNodes().add(filterNode);

                    }

                }
            }

        }

        return exception;

    }

    private boolean computeAnalysis(List<Instruction> computes, OrchaProgram orchaProgram, boolean exception) {

        if (computes != null) {

            List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

            for (Instruction c : computes) {

                ComputeInstruction compute = (ComputeInstruction) c;

                // search for the instruction right after the compute
                Instruction afterCompute = instructions.stream().filter(instruction -> instruction.getLineNumber() > compute.getLineNumber()).findFirst().orElse(null);

                if (afterCompute != null) {
                    switch (afterCompute.getCommand()) {
                        case "when":
                            WhenInstruction.ApplicationOrEventInExpression applicationOrEventInExpression = ((WhenInstruction) afterCompute).getApplicationsOrEvents().stream()
                                    .filter(application -> application.getName()
                                            .equals(compute.getApplication()))
                                    .findAny()
                                    .orElse(null);

                            if (applicationOrEventInExpression == null) {
                                exception = true;
                                String message = "when instruction following a compute does not contain the result of the compute in its condition : " + compute.getApplication() + ".";
                                log.error("Error at line " + afterCompute.getLineNumber() + " for the instruction (" + afterCompute.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, afterCompute.getLineNumber(), afterCompute.getInstruction()));
                            }
                            break;
                        default:
                    }
                }

                // where is the compute instruction into the list of nodes ?
                List<IntegrationNode> nodes = orchaProgram.getIntegrationGraph();
                int index = IntStream.range(0, nodes.size())
                        .filter(i -> nodes.get(i).getInstruction().equals(compute))
                        .findAny()
                        .getAsInt();

                nodes.get(index).setIntegrationPattern(IntegrationNode.IntegrationPattern.SERVICE_ACTIVATOR);

            }
        }

        return exception;

    }

    private boolean whenAnalysis(List<Instruction> whens, OrchaProgram orchaProgram, boolean exception) {

        if (whens != null) {

            for (Instruction instruction : whens) {

                WhenInstruction when = (WhenInstruction) instruction;

                // List of applications or event in the when expression like: when "(appli terminates) and (event receives)"
                List<WhenInstruction.ApplicationOrEventInExpression> applicationsOrEventsInExpression = when.getApplicationsOrEvents();

                // Check if there are related compute instruction like "compute appli..." or receive instruction like "receive event"
                for (WhenInstruction.ApplicationOrEventInExpression applicationOrEvent : applicationsOrEventsInExpression) {

                    String applicationOrEventName = applicationOrEvent.getName();

                    // Look the corresponding compute instruction like: compute appli...
                    IntegrationNode computeNode = orchaProgram.getIntegrationGraph().stream()
                            .filter(node -> node.getInstruction().getCommand().equals("compute") && ((ComputeInstruction) node.getInstruction()).getApplication().equals(applicationOrEventName))
                            .findFirst()
                            .orElse(null);

                    if (computeNode == null) {    // there is no compute associated

                        // Look for an event like: receive event...
                        IntegrationNode receiveNode = orchaProgram.getIntegrationGraph().stream()
                                .filter(node -> node.getInstruction().getCommand().equals("receive") && ((ReceiveInstruction) node.getInstruction()).getEvent().equals(applicationOrEventName))
                                .findFirst()
                                .orElse(null);

                        if (receiveNode == null) {
                            exception = true;
                            String message = "Unable to retreive " + applicationOrEventName + " from the when instruction (" + when.getInstruction() + ") in a compute or a receive.";
                            log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction()));
                        } else if (receiveNode.getInstruction().getLineNumber() > when.getLineNumber()) {
                            exception = true;
                            String message = "the event " + applicationOrEventName + " used in the when instruction was found in this receive instruction (" + receiveNode.getInstruction() + ") but after it.";
                            log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction()));
                        }

                    } else if (computeNode.getInstruction().getLineNumber() > when.getLineNumber()) {
                        exception = true;
                        String message = "the application " + applicationOrEventName + " used in the when instruction was found in this compute instruction (" + computeNode.getInstruction() + ") but after it.";
                        log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction()));
                    }

                }

                List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

                // search for the instruction right after the when
                Instruction afterWhen = instructions.stream().filter(instruc -> instruc.getLineNumber() > when.getLineNumber()).findFirst().orElse(null);

                if (afterWhen != null) {
                    switch (afterWhen.getCommand()) {
                        case "send":
                            // data in "send data..." should match with "when
                            SendInstruction send = (SendInstruction) afterWhen;
                            boolean dataFound = when.getApplicationsOrEvents().stream().anyMatch(applicationOrEventInExpression -> applicationOrEventInExpression.getName().equals(send.getData()));
                            if(dataFound == false){
                                exception = true;
                                String message = "send instruction sends a data (" + send.getData() + ") that does not match with any predicat in the when instruction (" + when.getInstruction() + ")";
                                log.error("Error at line " + send.getLineNumber() + " for the instruction (" + send.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction()));
                            }
                            break;
                        case "compute":
                            break;
                        default:
                            exception = true;
                            String message = "when instruction should be followed by a send or a compute.";
                            log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction()));
                    }
                } else {
                    exception = true;
                    String message = "when instruction should be followed by a send or a compute.";
                    log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction()));
                }


                // where is the when instruction into the list of nodes ?
                List<IntegrationNode> nodes = orchaProgram.getIntegrationGraph();
                int index = IntStream.range(0, nodes.size())
                        .filter(i -> nodes.get(i).getInstruction().equals(when))
                        .findAny()
                        .getAsInt();

                nodes.get(index).setIntegrationPattern(IntegrationNode.IntegrationPattern.AGGREGATOR);

                if (applicationsOrEventsInExpression.size() > 1) {
                    IntegrationNode resequencer = new IntegrationNode();
                    resequencer.setIntegrationPattern(IntegrationNode.IntegrationPattern.RESEQUENCER);
                    WhenInstruction fictitiousWhen = new WhenInstruction();
                    fictitiousWhen.setAggregationExpression(when.getAggregationExpression());
                    resequencer.setInstruction(fictitiousWhen);
                    resequencer.getNextIntegrationNodes().add(nodes.get(index));

                    nodes.add(index, resequencer);
                }
            }
        }

        return exception;
    }

    private boolean sendAnalysis(List<Instruction> sends, OrchaProgram orchaProgram, boolean exception) {

        if (sends != null) {

            List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

            for (Instruction s : sends) {

                SendInstruction send = (SendInstruction) s;

                // where is the send instruction into the list of nodes ?
                List<IntegrationNode> nodes = orchaProgram.getIntegrationGraph();
                int index = IntStream.range(0, nodes.size())
                        .filter(i -> nodes.get(i).getInstruction().equals(send))
                        .findAny()
                        .getAsInt();

                nodes.get(index).setIntegrationPattern(IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER);

            }
        }

        return exception;
    }
}
