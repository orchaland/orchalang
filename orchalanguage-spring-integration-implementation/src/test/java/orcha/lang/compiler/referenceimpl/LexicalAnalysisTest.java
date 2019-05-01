package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.springIntegration.SendInstructionForSpringIntegration;
import orcha.lang.compiler.referenceimpl.springIntegration.SpringIntegrationAutoConfiguration;
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionForSpringIntegration;
import orcha.lang.compiler.syntax.Instruction;
import orcha.lang.compiler.syntax.SendInstruction;
import orcha.lang.compiler.syntax.TitleInstruction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LexicalAnalysisTest {

    @Autowired
    LexicalAnalysis lexicalAnalysisForTest;

    @Test
    public void test() {

        try{
            Assert.assertTrue(lexicalAnalysisForTest != null);

            List<String> linesOfCode = new ArrayList<String>(
                    Arrays.asList(
                            "title: title",
                            "",
                            "receive order from customer",
                            "when \"order terminates\"",
                            "send order.number to customer"));

            OrchaProgram orchaProgram = lexicalAnalysisForTest.analysis(linesOfCode);

            Assert.assertNotNull(orchaProgram);
            Assert.assertNotNull(orchaProgram.getIntegrationGraph());
            Assert.assertNotNull(orchaProgram.getOrchaMetadata());

            OrchaMetadata orchaMetadata = orchaProgram.getOrchaMetadata();
            List<Instruction> metadata = orchaMetadata.getMetadata();
            Assert.assertNotNull(metadata);
            Assert.assertEquals(metadata.size(), 1);
            Instruction instruction = metadata.get(0);
            Assert.assertTrue(instruction instanceof TitleInstruction);


            List<IntegrationNode> graphOfInstructions = orchaProgram.getIntegrationGraph();

            Assert.assertTrue(graphOfInstructions.size() == 3);

            IntegrationNode integrationNode = graphOfInstructions.get(1);
            Assert.assertNotNull(integrationNode);
            Instruction inst = integrationNode.getInstruction();
            inst.analysis();
            Assert.assertNotNull(inst);
            Assert.assertTrue(inst instanceof WhenInstructionForSpringIntegration);
            WhenInstructionForSpringIntegration whenInstruction = (WhenInstructionForSpringIntegration)inst;
            Assert.assertEquals(whenInstruction.getLineNumber(), 4);
            Assert.assertTrue(whenInstruction.getCommand().equals("when"));

            integrationNode = graphOfInstructions.get(2);
            Assert.assertNotNull(integrationNode);
            inst = integrationNode.getInstruction();
            Assert.assertNotNull(inst);
            inst.analysis();
            Assert.assertTrue(inst instanceof SendInstructionForSpringIntegration);
            SendInstructionForSpringIntegration sendInstruction = (SendInstructionForSpringIntegration)inst;
            Assert.assertEquals(sendInstruction.getLineNumber(), 5);
            Assert.assertTrue(sendInstruction.getCommand().equals("send"));
            Assert.assertEquals(sendInstruction.getVariables(), "payload.number");

        }catch(OrchaCompilationException e){
            Assert.fail(e.getMessage());
        }
    }

}
