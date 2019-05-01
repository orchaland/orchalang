package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import orcha.lang.compiler.syntax.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Global vrriables are variables global to an Orcha program.
 * The following example uses a variable maximumNumber in the when instruction at line 3:
 *
 *    1     receive request from receivers
 *    2     compute validateAndAddRequest with request, authorities.acceptanceCondition
 *    3     when "validateAndAddRequest terminates condition numberOfValidRequests==receivers.maximumNumber"
 *    4     compute negociateTransportation with validateAndAddRequest
 *    5     when "negociateTransportation terminates"
 *    6     compute informTransporter with negociateTransportation.result, validateAndAddRequest.badRequests
 *    7     when "informTransporter terminates"
 *    8     send informTransporter.result to authorities
 *
 *
 * Such a variable must be attached to a source of an event (request in the previous example),
 * an application (validateAndAddRequest holds a global variable named badRequests and used at line 6 or
 * a destination of a send instruction (authorities has a global variable named acceptanceCondition used at line 2.
 *
 * These are global variable because they do not belong to the messages that across the Orcha program:
 * in the previous example request is a local variable because it changes each time an event is received
 * but maximumNumber do not because the source receivers is unique.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GlobalVariable {

    @Autowired
    SemanticAnalysis semanticAnalysisForTest;

    @Test
    public void test(){

        try {

            Instruction titleInstruction = new TitleInstruction("title: organize transportation");
            titleInstruction.setLineNumber(1);
            titleInstruction.analysis();
            OrchaMetadata orchaMetadata = new OrchaMetadata();
            orchaMetadata.add(titleInstruction);

            List<IntegrationNode> integrationNodes = new ArrayList<IntegrationNode>();

            Instruction instruction1 = new ReceiveInstruction("receive request from receivers");
            instruction1.setLineNumber(2);
            instruction1.analysis();

            integrationNodes.add(new IntegrationNode(instruction1));


            Instruction instruction2 = new ComputeInstruction("compute validateAndAddRequest with request, authorities.acceptanceCondition");
            instruction2.setLineNumber(3);
            instruction2.analysis();

            integrationNodes.add(new IntegrationNode(instruction2));


            Instruction instruction3 = new WhenInstruction("when \"validateAndAddRequest terminates condition numberOfValidRequests==receivers.maximumNumber\"");
            instruction3.setLineNumber(4);
            instruction3.analysis();

            integrationNodes.add(new IntegrationNode(instruction3));


            Instruction instruction4 = new ComputeInstruction("compute negociateTransportation with validateAndAddRequest");
            instruction4.setLineNumber(5);
            instruction4.analysis();

            integrationNodes.add(new IntegrationNode(instruction4));


            Instruction instruction5 = new WhenInstruction("when \"negociateTransportation terminates\"");
            instruction5.setLineNumber(6);
            instruction5.analysis();

            integrationNodes.add(new IntegrationNode(instruction5));


            Instruction instruction6 = new ComputeInstruction("compute informTransporter with negociateTransportation.result, validateAndAddRequest.badRequests");
            instruction6.setLineNumber(7);
            instruction6.analysis();

            integrationNodes.add(new IntegrationNode(instruction6));


            Instruction instruction7 = new WhenInstruction("when \"informTransporter terminates\"");
            instruction7.setLineNumber(8);
            instruction7.analysis();

            integrationNodes.add(new IntegrationNode(instruction7));


            Instruction instruction8 = new WhenInstruction("send informTransporter.result to authorities");
            instruction8.setLineNumber(9);
            instruction8.analysis();

            integrationNodes.add(new IntegrationNode(instruction8));


            OrchaProgram orchaProgram = new OrchaProgram();
            orchaProgram.setIntegrationGraph(integrationNodes);
            orchaProgram.setOrchaMetadata(orchaMetadata);

            orchaProgram = semanticAnalysisForTest.analysis(orchaProgram);


            integrationNodes = orchaProgram.getIntegrationGraph();

            Assert.assertTrue(integrationNodes.size() == 8);


            Instruction instruction = integrationNodes.stream().filter(node -> node.getInstruction().getLineNumber() == 3).map(node -> node.getInstruction()).findAny().orElse(null);
            Assert.assertNotNull(instruction);
            Assert.assertTrue(instruction instanceof ComputeInstruction);
            ComputeInstruction computeInstruction = (ComputeInstruction)instruction;
            computeInstruction.getParameters();



        } catch (OrchaCompilationException e){

        }



    }
}
