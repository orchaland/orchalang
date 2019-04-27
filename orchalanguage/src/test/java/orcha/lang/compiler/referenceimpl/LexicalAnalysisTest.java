package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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

            OrchaProgram orchaSmartContract = lexicalAnalysisForTest.analysis("test.groovy");

            Assert.assertNotNull(orchaSmartContract);
            Assert.assertNotNull(orchaSmartContract.getIntegrationGraph());
            Assert.assertNotNull(orchaSmartContract.getOrchaMetadata());

            List<IntegrationNode> graphOfInstructions = orchaSmartContract.getIntegrationGraph();
            Assert.assertTrue(graphOfInstructions.size() == 2);
            Assert.assertNotNull(graphOfInstructions.get(0).getInstruction());
            Assert.assertTrue(graphOfInstructions.get(0).getInstruction().getCommand() == Instruction.Command.RECEIVE);
            Assert.assertTrue(graphOfInstructions.get(0).getInstruction().getLineNumber() == 5);
            Assert.assertTrue(graphOfInstructions.get(0).getInstruction().getId() == 1);
            Assert.assertTrue(graphOfInstructions.get(0).getInstruction().getInstruction().equals("receive order from customer"));

        }catch(OrchaCompilationException e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMetadata() {
        // read the file goodFile.orcha from src/test/orcha and check if metadata are correct
    }

    @Test
    public void errors() {
        // test errors
    }
}
