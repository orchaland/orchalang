package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.OrchaProgram;
import orcha.lang.compiler.Transpiler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TranspilerTest {

    @Autowired
    Transpiler transpiler;

    @Test
    public void test(){
        OrchaProgram orchaProgram = new OrchaProgram();
        //transpiler.transpile(orchaProgram);
    }

}
