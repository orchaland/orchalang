package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
                            "title \"title\"",
                            "",
                            "receive order from customer",
                            "when \"something terminates\""));

            OrchaProgram orchaSmartContract = lexicalAnalysisForTest.analysis(linesOfCode);

            Assert.assertNotNull(orchaSmartContract);
            Assert.assertNotNull(orchaSmartContract.getIntegrationGraph());
            Assert.assertNotNull(orchaSmartContract.getOrchaMetadata());

            //Assert.assertEquals(orchaSmartContract.getOrchaMetadata().getTitle(), "title");

            List<IntegrationNode> graphOfInstructions = orchaSmartContract.getIntegrationGraph();

            Assert.assertTrue(graphOfInstructions.size() == 2);

            Assert.assertNotNull(graphOfInstructions.get(0).getInstruction());
            Assert.assertTrue(graphOfInstructions.get(0).getInstruction().getCommand().equals("receive"));
            Assert.assertTrue(graphOfInstructions.get(0).getInstruction().getLineNumber() == 3);
            Assert.assertTrue(graphOfInstructions.get(0).getInstruction().getInstruction().equals("receive order from customer"));

            Assert.assertNotNull(graphOfInstructions.get(1).getInstruction());
            Assert.assertTrue(graphOfInstructions.get(1).getInstruction().getCommand().equals("when"));
            Assert.assertTrue(graphOfInstructions.get(1).getInstruction().getLineNumber() == 4);
            Assert.assertTrue(graphOfInstructions.get(1).getInstruction().getInstruction().equals("when \"something terminates\""));

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
