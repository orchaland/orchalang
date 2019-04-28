package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SemanticAnalysisTest {

    @Autowired
    SemanticAnalysis semanticAnalysisForTest;

    @Test
    public void receiveInstruction() {

        try{

            List<IntegrationNode> integrationNodes = new ArrayList<IntegrationNode>();

            Instruction instruction1 = new ReceiveInstruction("receive order from customer");
            instruction1.setLineNumber(1);
            instruction1.analysis();

            integrationNodes.add(new IntegrationNode(instruction1));

            Instruction instruction2 = new ComputeInstruction("compute checkOrder with order");
            instruction2.setLineNumber(2);
            instruction2.analysis();

            integrationNodes.add(new IntegrationNode(instruction2));

            Instruction instruction3 = new ReceiveInstruction("receive travelInfo from travelAgency");
            instruction3.setLineNumber(3);
            instruction3.analysis();

            integrationNodes.add(new IntegrationNode(instruction3));

            Instruction instruction4 = new SendInstruction("send travelInfo to customer");
            instruction4.setLineNumber(4);
            instruction4.analysis();

            integrationNodes.add(new IntegrationNode(instruction4));

            Instruction instruction5 = new ReceiveInstruction("receive event from file");
            instruction5.setLineNumber(5);
            instruction5.analysis();

            integrationNodes.add(new IntegrationNode(instruction5));

            Instruction instruction6 = new WhenInstruction("when \"event receives\"");
            instruction6.setLineNumber(6);
            instruction6.analysis();

            integrationNodes.add(new IntegrationNode(instruction6));

            Instruction instruction7 = new SendInstruction("send event to fileSystem");
            instruction7.setLineNumber(7);
            instruction7.analysis();

            integrationNodes.add(new IntegrationNode(instruction7));

            OrchaProgram orchaSmartContract = new OrchaProgram();
            orchaSmartContract.setIntegrationGraph(integrationNodes);

            orchaSmartContract = semanticAnalysisForTest.analysis(orchaSmartContract);

            integrationNodes = orchaSmartContract.getIntegrationGraph();

            Assert.assertTrue(integrationNodes.size() == 7);

            Assert.assertTrue(integrationNodes.get(0).getInstruction().equals(instruction1));
            Assert.assertNotNull(integrationNodes.get(0).getNextIntegrationNodes());
            Assert.assertTrue(integrationNodes.get(0).getNextIntegrationNodes().size() == 1);
            Assert.assertNotNull(integrationNodes.get(0).getNextIntegrationNodes().get(0).getInstruction());
            Assert.assertTrue(integrationNodes.get(0).getNextIntegrationNodes().get(0).getInstruction().equals(instruction2));

            Assert.assertTrue(integrationNodes.get(1).getInstruction().equals(instruction2));

            Assert.assertTrue(integrationNodes.get(2).getInstruction().equals(instruction3));
            Assert.assertNotNull(integrationNodes.get(2).getNextIntegrationNodes());
            Assert.assertTrue(integrationNodes.get(2).getNextIntegrationNodes().size() == 1);
            Assert.assertNotNull(integrationNodes.get(2).getNextIntegrationNodes().get(0).getInstruction());
            Assert.assertTrue(integrationNodes.get(2).getNextIntegrationNodes().get(0).getInstruction().equals(instruction4));


            Assert.assertTrue(integrationNodes.get(3).getInstruction().equals(instruction4));

            Assert.assertTrue(integrationNodes.get(4).getInstruction().equals(instruction5));
            Assert.assertNotNull(integrationNodes.get(4).getNextIntegrationNodes());
            Assert.assertTrue(integrationNodes.get(4).getNextIntegrationNodes().size() == 1);
            Assert.assertNotNull(integrationNodes.get(4).getNextIntegrationNodes().get(0).getInstruction());
            Assert.assertTrue(integrationNodes.get(4).getNextIntegrationNodes().get(0).getInstruction().equals(instruction6));

        } catch(OrchaCompilationException e){
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void receivesInstructionWithTheSameEvent() {

        try {

            List<IntegrationNode> integrationNodes = new ArrayList<IntegrationNode>();

            Instruction instruction1 = new ReceiveInstruction("receive order from customer");
            instruction1.setLineNumber(1);
            instruction1.analysis();

            integrationNodes.add(new IntegrationNode(instruction1));

            Instruction instruction2 = new ComputeInstruction("compute checkOrder with order");
            instruction2.setLineNumber(2);
            instruction2.analysis();

            integrationNodes.add(new IntegrationNode(instruction2));


            Instruction instruction3 = new ReceiveInstruction("receive order from customer");
            instruction3.setLineNumber(3);
            instruction3.analysis();

            integrationNodes.add(new IntegrationNode(instruction3));

            Instruction instruction4 = new ComputeInstruction("compute sendOrder with order");
            instruction4.setLineNumber(4);
            instruction4.analysis();

            integrationNodes.add(new IntegrationNode(instruction4));


            OrchaProgram orchaProgram = new OrchaProgram();
            orchaProgram.setIntegrationGraph(integrationNodes);

            orchaProgram = semanticAnalysisForTest.analysis(orchaProgram);

            integrationNodes = orchaProgram.getIntegrationGraph();

            // 4 nodes + 1 fictitious node
            Assert.assertTrue(integrationNodes.size() == 5);

            List<IntegrationNode> receives = integrationNodes.stream().filter(node -> node.getInstruction().getCommand().equals("receive")).collect(Collectors.toList());
            Assert.assertNotNull(receives);
            Assert.assertTrue(receives.size() == 3);

            IntegrationNode integrationNode = receives.get(0);
            Assert.assertEquals(integrationNode.getIntegrationPattern(), IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER);
            Instruction instruction = receives.get(0).getInstruction();
            Assert.assertTrue(instruction instanceof ReceiveInstruction);
            ReceiveInstruction receiveInstruction = (ReceiveInstruction) instruction;
            String event = receiveInstruction.getEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals(event, "order");
            String source = receiveInstruction.getSource();
            Assert.assertNotNull(source);
            Assert.assertEquals(source, "customer");

            List<IntegrationNode> adjacentNodes = receives.get(0).getNextIntegrationNodes();
            Assert.assertNotNull(adjacentNodes);
            Assert.assertTrue(adjacentNodes.size() == 1);
            integrationNode = adjacentNodes.get(0);
            Assert.assertEquals(integrationNode.getIntegrationPattern(), IntegrationNode.IntegrationPattern.MESSAGE_ROUTER);
            instruction = adjacentNodes.get(0).getInstruction();
            Assert.assertTrue(instruction instanceof ReceiveInstruction);
            receiveInstruction = (ReceiveInstruction) instruction;
            event = receiveInstruction.getEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals(event, "order");
            source = receiveInstruction.getSource();
            Assert.assertNotNull(source);
            Assert.assertEquals(source, "customer");

            adjacentNodes = integrationNode.getNextIntegrationNodes();
            Assert.assertNotNull(adjacentNodes);
            Assert.assertTrue(adjacentNodes.size() == 2);

            integrationNode = adjacentNodes.get(0);
            instruction = integrationNode.getInstruction();
            Assert.assertTrue(instruction instanceof ReceiveInstruction);
            receiveInstruction = (ReceiveInstruction) instruction;
            event = receiveInstruction.getEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals(event, "order");
            source = receiveInstruction.getSource();
            Assert.assertNotNull(source);
            Assert.assertEquals(source, "customer");

            integrationNode = adjacentNodes.get(1);
            instruction = integrationNode.getInstruction();
            Assert.assertTrue(instruction instanceof ReceiveInstruction);
            receiveInstruction = (ReceiveInstruction) instruction;
            event = receiveInstruction.getEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals(event, "order");
            source = receiveInstruction.getSource();
            Assert.assertNotNull(source);
            Assert.assertEquals(source, "customer");

        }catch(OrchaCompilationException e){
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void receivesInstructionWithTheSameEventAndSameCondition() {

        try{

            List<IntegrationNode> integrationNodes = new ArrayList<IntegrationNode>();

            Instruction instruction1 = new ReceiveInstruction("receive order from customer condition name==\"Ben\"");
            instruction1.setLineNumber(1);
            instruction1.analysis();

            integrationNodes.add(new IntegrationNode(instruction1));

            Instruction instruction2 = new ComputeInstruction("compute checkOrder with order");
            instruction2.setLineNumber(2);
            instruction2.analysis();

            integrationNodes.add(new IntegrationNode(instruction2));


            Instruction instruction3 = new ReceiveInstruction("receive order from customer condition name==\"Ben\"");
            instruction3.setLineNumber(3);
            instruction3.analysis();

            integrationNodes.add(new IntegrationNode(instruction3));

            Instruction instruction4 = new ComputeInstruction("compute sendOrder with order");
            instruction4.setLineNumber(4);
            instruction4.analysis();

            integrationNodes.add(new IntegrationNode(instruction4));


            OrchaProgram orchaProgram = new OrchaProgram();
            orchaProgram.setIntegrationGraph(integrationNodes);

            orchaProgram = semanticAnalysisForTest.analysis(orchaProgram);

            integrationNodes = orchaProgram.getIntegrationGraph();

            // 4 nodes + 1 fictitious node
            Assert.assertTrue(integrationNodes.size() == 5);

            List<IntegrationNode> receives = integrationNodes.stream().filter(node -> node.getInstruction().getCommand().equals("receive")).collect(Collectors.toList());
            Assert.assertNotNull(receives);
            Assert.assertTrue(receives.size() == 3);

            IntegrationNode integrationNode = receives.get(0);
            Assert.assertEquals(integrationNode.getIntegrationPattern(), IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER);
            Instruction instruction = receives.get(0).getInstruction();
            Assert.assertTrue(instruction instanceof ReceiveInstruction);
            ReceiveInstruction receiveInstruction = (ReceiveInstruction) instruction;
            String event = receiveInstruction.getEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals(event, "order");
            String source = receiveInstruction.getSource();
            Assert.assertNotNull(source);
            Assert.assertEquals(source, "customer");

            List<IntegrationNode> adjacentNodes = receives.get(0).getNextIntegrationNodes();
            Assert.assertNotNull(adjacentNodes);
            Assert.assertTrue(adjacentNodes.size() == 1);
            integrationNode = adjacentNodes.get(0);
            Assert.assertEquals(integrationNode.getIntegrationPattern(), IntegrationNode.IntegrationPattern.MESSAGE_FILTER);
            instruction = adjacentNodes.get(0).getInstruction();
            Assert.assertTrue(instruction instanceof ReceiveInstruction);
            receiveInstruction = (ReceiveInstruction) instruction;
            event = receiveInstruction.getEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals(event, "order");
            source = receiveInstruction.getSource();
            Assert.assertNotNull(source);
            Assert.assertEquals(source, "customer");
            String condition = receiveInstruction.getCondition();
            Assert.assertNotNull(condition);
            Assert.assertEquals(condition, "name==\"Ben\"");

            adjacentNodes = integrationNode.getNextIntegrationNodes();
            Assert.assertNotNull(adjacentNodes);
            Assert.assertTrue(adjacentNodes.size() == 1);
            integrationNode = adjacentNodes.get(0);
            Assert.assertEquals(integrationNode.getIntegrationPattern(), IntegrationNode.IntegrationPattern.MESSAGE_ROUTER);
            instruction = adjacentNodes.get(0).getInstruction();
            Assert.assertTrue(instruction instanceof ReceiveInstruction);
            receiveInstruction = (ReceiveInstruction) instruction;
            event = receiveInstruction.getEvent();
            Assert.assertNotNull(event);
            Assert.assertEquals(event, "order");
            source = receiveInstruction.getSource();
            Assert.assertNotNull(source);
            Assert.assertEquals(source, "customer");

        }catch(OrchaCompilationException e){
            Assert.fail(e.getMessage());
        }


    }

    @Test
    public void whenInstruction() {

        try{

            List<IntegrationNode> integrationNodes = new ArrayList<IntegrationNode>();

            Instruction instruction1 = new ReceiveInstruction("receive travelInfo from travelAgency");
            instruction1.setLineNumber(1);
            instruction1.analysis();

            integrationNodes.add(new IntegrationNode(instruction1));


            Instruction instruction2 = new ComputeInstruction("compute checkOrder with travelInfo");
            instruction2.setLineNumber(2);
            instruction2.analysis();

            integrationNodes.add(new IntegrationNode(instruction2));


            Instruction instruction3 = new WhenInstruction("when \"(checkOrder terminates) and (travelInfo receives)\"");
            instruction3.setLineNumber(3);
            instruction3.analysis();

            integrationNodes.add(new IntegrationNode(instruction3));


            Instruction instruction4 = new SendInstruction("send checkOrder to customer");
            instruction4.setLineNumber(4);
            instruction4.analysis();

            integrationNodes.add(new IntegrationNode(instruction4));


            OrchaProgram orchaSmartContract = new OrchaProgram();
            orchaSmartContract.setIntegrationGraph(integrationNodes);

            orchaSmartContract = semanticAnalysisForTest.analysis(orchaSmartContract);

            integrationNodes = orchaSmartContract.getIntegrationGraph();

            Assert.assertTrue(integrationNodes.size() == 5);

            Assert.assertTrue(integrationNodes.get(3).getInstruction().equals(instruction3));

            IntegrationNode resequencer = integrationNodes.get(2);
            Assert.assertEquals(resequencer.getIntegrationPattern(), IntegrationNode.IntegrationPattern.RESEQUENCER);

            List<IntegrationNode> nextNodes = resequencer.getNextIntegrationNodes();
            Assert.assertNotNull(nextNodes);
            Assert.assertEquals(nextNodes.size(), 1);
            IntegrationNode aggregator = nextNodes.get(0);
            Assert.assertNotNull(aggregator);
            Assert.assertEquals(aggregator.getIntegrationPattern(), IntegrationNode.IntegrationPattern.AGGREGATOR);
            Assert.assertTrue(aggregator.getInstruction().equals(instruction3));

            nextNodes = aggregator.getNextIntegrationNodes();
            Assert.assertNotNull(nextNodes);
            Assert.assertEquals(nextNodes.size(), 0);

        }catch(OrchaCompilationException e){
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void sendInstruction() {

        try{

            List<IntegrationNode> integrationNodes = new ArrayList<IntegrationNode>();

            Instruction instruction1 = new SendInstruction("send data to output");
            instruction1.setLineNumber(1);
            instruction1.analysis();

            integrationNodes.add(new IntegrationNode(instruction1));

            OrchaProgram orchaSmartContract = new OrchaProgram();
            orchaSmartContract.setIntegrationGraph(integrationNodes);

            orchaSmartContract = semanticAnalysisForTest.analysis(orchaSmartContract);

            integrationNodes = orchaSmartContract.getIntegrationGraph();

            Assert.assertTrue(integrationNodes.size() == 1);

            IntegrationNode adapter = integrationNodes.get(0);
            Assert.assertTrue(adapter.getInstruction().equals(instruction1));
            Assert.assertEquals(adapter.getIntegrationPattern(), IntegrationNode.IntegrationPattern.CHANNEL_ADAPTER);

            List<IntegrationNode> nextNodes = adapter.getNextIntegrationNodes();
            Assert.assertNotNull(nextNodes);
            Assert.assertEquals(nextNodes.size(), 0);

        }catch(OrchaCompilationException e){
            Assert.fail(e.getMessage());
        }


    }

}

