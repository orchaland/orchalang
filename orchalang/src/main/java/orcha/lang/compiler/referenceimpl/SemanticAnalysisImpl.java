package orcha.lang.compiler.referenceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import orcha.lang.compiler.*;
import orcha.lang.compiler.syntax.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SemanticAnalysisImpl implements SemanticAnalysis {

    private static Logger log = LoggerFactory.getLogger(SemanticAnalysisImpl.class);

    @Override
    public OrchaProgram analysis(OrchaProgram orchaProgram) throws OrchaCompilationException {

        log.info("Semantic analysis of the orcha program.");

        List<OrchaCompilationException> exceptions = new ArrayList<OrchaCompilationException>();

        OrchaMetadata metadata = orchaProgram.getOrchaMetadata();
        this.metadataAnalysis(metadata, exceptions);

        // The complete list of instructions in the orcha program
        List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

        // Group instructions by receive instruction, compute, when and sendf
        Map<String, List<Instruction>> instructionsByCommand = instructions.stream()
                .collect(Collectors.groupingBy(Instruction::getCommand));

        List<Instruction> receives = instructionsByCommand.get("receive");
        this.receiveAnalysis(receives, orchaProgram, exceptions);

        List<Instruction> computes = instructionsByCommand.get("compute");
        this.computeAnalysis(computes, orchaProgram, exceptions);

        List<Instruction> whens = instructionsByCommand.get("when");
        this.whenAnalysis(whens, orchaProgram, exceptions);

        List<Instruction> sends = instructionsByCommand.get("send");
        this.sendAnalysis(sends, orchaProgram, exceptions);

        if (exceptions.size() > 0) {
            throw new OrchaCompilationException("Semantic analysis error.");
        }

        log.info("Semantic analysis of the orcha program \"" + orchaProgram.getOrchaMetadata().getTitle() + "\" complete successfuly.");

        return orchaProgram;
    }

    private void metadataAnalysis(OrchaMetadata orchaMetadata, List<OrchaCompilationException> exceptions){

        log.info("Metadata analysis of the orcha program begins.");

        List<Instruction> metadata = orchaMetadata.getMetadata();
        if(metadata == null){
            String message = "title is missing";
            OrchaCompilationException exception = new OrchaCompilationException(message);
            log.error("Error cause by: " + message, exception);
            exceptions.add(exception);
            return;
        }

        TitleInstruction titleInstruction = (TitleInstruction) metadata.stream().filter(instruction -> instruction instanceof TitleInstruction).findAny().orElse(null);
        if(titleInstruction == null){
            String message = "title is missing";
            OrchaCompilationException exception = new OrchaCompilationException(message);
            log.error("Error cause by: " + message, exception);
            exceptions.add(exception);
            return;
        }

        String title = titleInstruction.getTitle();
        if(title == null){
            String message = "title is missing";
            OrchaCompilationException exception = new OrchaCompilationException(message, titleInstruction.getLineNumber(), titleInstruction.getInstruction());
            log.error("Error at line " + titleInstruction.getLineNumber() + " for the instruction (" + titleInstruction.getInstruction() + ") cause by: " + message, exception);
            exceptions.add(exception);
        }

        log.info("Metadata analysis of the orcha program \"" + orchaMetadata.getTitle() + "\" complete successfuly.");

    }

    private void receiveAnalysis(List<Instruction> receives, OrchaProgram orchaProgram, List<OrchaCompilationException> exceptions){

        if (receives != null) {

            List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

            for (Instruction r : receives) {

                ReceiveInstruction receive = (ReceiveInstruction) r;

                log.info("Semantic analysis of the receive instruction: " + receive);

                // search for the instruction right after the receive
                Instruction afterReceive = instructions.stream().filter(instruction -> instruction.getLineNumber() > receive.getLineNumber()).findFirst().orElse(null);

                if (afterReceive == null) {

                    String message = "receive instruction should be followed by a when, a compute, or a send instruction";
                    OrchaCompilationException exception = new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction());
                    log.error("Error at line " + receive.getLineNumber() + " for the instruction (" + receive.getInstruction() + ") cause by: " + message, exception);
                    exceptions.add(exception);

                } else {

                    switch (afterReceive.getCommand()) {
                        case "compute":
                            if (((ComputeInstruction) afterReceive).getParameters().contains(receive.getEvent()) == false) {
                                String message = "compute instruction following a receive does not contain a with as excepted: with should contain " + receive.getEvent();
                                OrchaCompilationException exception = new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction());
                                log.error("Error at line " + afterReceive.getLineNumber() + " for the instruction (" + afterReceive.getInstruction() + ") cause by: " + message, exception);
                                exceptions.add(exception);
                            }
                            break;
                        case "send":
                            if (((SendInstruction) afterReceive).getData().equals(receive.getEvent()) == false) {
                                String message = "send instruction does not contain the received event: " + receive.getEvent() + ".";
                                OrchaCompilationException exception = new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction());
                                log.error("Error at line " + afterReceive.getLineNumber() + " for the instruction (" + afterReceive.getInstruction() + ") cause by: " + message, exception);
                                exceptions.add(exception);
                            }
                            break;
                        case "when":
                            WhenInstruction whenInstruction = (WhenInstruction) afterReceive;
                            WhenInstruction.ApplicationOrEventInExpression applicationOrEventInExpression = whenInstruction.getApplicationsOrEvents().stream().filter(event -> event.getName().equals(receive.getEvent())).findAny().orElse(null);
                            if (applicationOrEventInExpression == null) {
                                String message = "when instruction following a receive does not contain the received event in its condition : " + receive.getEvent() + ".";
                                OrchaCompilationException exception = new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction());
                                log.error("Error at line " + whenInstruction.getLineNumber() + " for the instruction (" + whenInstruction.getInstruction() + ") cause by: " + message, exception);
                                exceptions.add(exception);
                            }
                            break;
                        default:
                            String message = "receive instruction should be followed by a when, a compute, or a send instruction";
                            OrchaCompilationException exception = new OrchaCompilationException(message, afterReceive.getLineNumber(), afterReceive.getInstruction());
                            log.error("Error at line " + receive.getLineNumber() + " for the instruction (" + receive.getInstruction() + ") cause by: " + message, exception);
                            exceptions.add(exception);
                    }

                    // search for the instruction node containing the receive instruction => then add a node has an adjacent node
                    IntegrationNode receiveNode = orchaProgram.getIntegrationGraph().stream().filter(instructionNode -> instructionNode.getInstruction() == receive).findFirst().orElse(null);
                    receiveNode.getNextIntegrationNodes().add(new IntegrationNode(afterReceive));

                }


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
                        String message = "all the receive instructions having " + event + " as event should have the same source also.";
                        OrchaCompilationException exception = new OrchaCompilationException(message);
                        log.error(message, exception);
                        exceptions.add(exception);
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

    }

    private void computeAnalysis(List<Instruction> computes, OrchaProgram orchaProgram, List<OrchaCompilationException> exceptions){

        if (computes != null) {

            List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

            for (Instruction c : computes) {

                ComputeInstruction compute = (ComputeInstruction) c;

                log.info("Semantic analysis of the compute instruction: " + compute);

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
                                String message = "when instruction following a compute does not contain the result of the compute in its condition : " + compute.getApplication() + ".";
                                OrchaCompilationException exception = new OrchaCompilationException(message, afterCompute.getLineNumber(), afterCompute.getInstruction());
                                log.error("Error at line " + afterCompute.getLineNumber() + " for the instruction (" + afterCompute.getInstruction() + ") cause by: " + message, exception);
                                exceptions.add(exception);
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

    }

    private void whenAnalysis(List<Instruction> whens, OrchaProgram orchaProgram, List<OrchaCompilationException> exceptions){

        if (whens != null) {

            for (Instruction instruction : whens) {

                WhenInstruction when = (WhenInstruction) instruction;

                log.info("Semantic analysis of the when instruction: " + when);

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
                            String message = "Unable to retreive " + applicationOrEventName + " from the when instruction (" + when.getInstruction() + ") in a compute or a receive.";
                            OrchaCompilationException exception = new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction());
                            log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, exception);
                            exceptions.add(exception);
                        } else if (receiveNode.getInstruction().getLineNumber() > when.getLineNumber()) {
                            String message = "the event " + applicationOrEventName + " used in the when instruction was found in this receive instruction (" + receiveNode.getInstruction() + ") but after it.";
                            OrchaCompilationException exception = new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction());
                            log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, exception);
                            exceptions.add(exception);
                        }

                    } else if (computeNode.getInstruction().getLineNumber() > when.getLineNumber()) {
                        String message = "the application " + applicationOrEventName + " used in the when instruction was found in this compute instruction (" + computeNode.getInstruction() + ") but after it.";
                        OrchaCompilationException exception = new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction());
                        log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, exception);
                        exceptions.add(exception);
                    }

                };

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
                                String message = "send instruction sends a data (" + send.getData() + ") that does not match with any predicat in the when instruction (" + when.getInstruction() + ")";
                                OrchaCompilationException exception = new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction());
                                log.error("Error at line " + send.getLineNumber() + " for the instruction (" + send.getInstruction() + ") cause by: " + message, exception);
                                exceptions.add(exception);
                            }
                            break;
                        case "compute":
                            break;
                        default:
                            String message = "when instruction should be followed by a send or a compute.";
                            OrchaCompilationException exception = new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction());
                            log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, exception);
                            exceptions.add(exception);
                    }
                } else {
                    String message = "when instruction should be followed by a send or a compute.";
                    OrchaCompilationException exception = new OrchaCompilationException(message, when.getLineNumber(), when.getInstruction());
                    log.error("Error at line " + when.getLineNumber() + " for the instruction (" + when.getInstruction() + ") cause by: " + message, exception);
                    exceptions.add(exception);
                }


                // where is the when instruction into the list of nodes ?
                List<IntegrationNode> nodes = orchaProgram.getIntegrationGraph();
                int index = IntStream.range(0, nodes.size())
                        .filter(i -> nodes.get(i).getInstruction().equals(when))
                        .findAny()
                        .getAsInt();

                nodes.get(index).setIntegrationPattern(IntegrationNode.IntegrationPattern.AGGREGATOR);

                if (applicationsOrEventsInExpression.size() > 1) {
                    WhenInstruction fictitiousWhen = new WhenInstruction();
                    fictitiousWhen.setAggregationExpression(when.getAggregationExpression());
                    IntegrationNode resequencer = new IntegrationNode(fictitiousWhen);
                    resequencer.setIntegrationPattern(IntegrationNode.IntegrationPattern.RESEQUENCER);
                    resequencer.getNextIntegrationNodes().add(nodes.get(index));

                    nodes.add(index, resequencer);
                }
            }
        }

    }

    private void sendAnalysis(List<Instruction> sends, OrchaProgram orchaProgram, List<OrchaCompilationException> exceptions){

        if (sends != null) {

            List<Instruction> instructions = orchaProgram.getIntegrationGraph().stream().map(node -> node.getInstruction()).collect(Collectors.toList());

            for (Instruction s : sends) {

                SendInstruction send = (SendInstruction) s;

                log.info("Semantic analysis of the send instruction: " + send);

                // where is the send instruction into the list of nodes ?
                List<IntegrationNode> nodes = orchaProgram.getIntegrationGraph();
                int index = IntStream.range(0, nodes.size())
                        .filter(i -> nodes.get(i).getInstruction().equals(send))
                        .findAny()
                        .getAsInt();

                nodes.get(index).setIntegrationPattern(IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER);

                String variables = send.getVariables();

                if(variables != null && variables.equals("payload.result")==false){
                    IntegrationNode translator = new IntegrationNode(send);
                    translator.setIntegrationPattern(IntegrationNode.IntegrationPattern.MESSAGE_TRANSLATOR);
                    translator.getNextIntegrationNodes().add(nodes.get(index));
                    nodes.set(index, translator);
                }

            }
        }
    }
}
