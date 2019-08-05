package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.OrchaCompilationException;
import orcha.lang.compiler.Preprocessing;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"orcha.pathToOrchaSourceFile=src/test/orcha/source"})
public class PreprocessingTest {

    @Autowired
    Preprocessing preprocessingForTest;

    @Test
    public void basicTest() {

        Assert.assertTrue(preprocessingForTest != null);

        try{

            List<String> instructions = preprocessingForTest.process("goodFile.orcha");

            List<String> computeInstructions = instructions.stream().filter(instruction -> instruction.startsWith("compute ")).collect(Collectors.toList());
            Assert.assertTrue(computeInstructions.size() == 1);
            Assert.assertTrue(computeInstructions.get(0).endsWith("checkOrder with order.value"));

        } catch(OrchaCompilationException exception){
            Assert.fail(exception.getMessage());
        }

    }

    @Test(expected = OrchaCompilationException.class)
    public void unexistingFile() throws OrchaCompilationException {
        preprocessingForTest.process("ezefzdfsdvffgbszes.orcha");
    }

}
