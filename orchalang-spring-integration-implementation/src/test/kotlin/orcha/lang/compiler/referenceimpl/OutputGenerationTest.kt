package orcha.lang.compiler.referenceimpl

import orcha.lang.compiler.OrchaProgram
import orcha.lang.compiler.OutputGeneration
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class OutputGenerationTest {

    @Autowired
    internal var outputGeneration: OutputGeneration? = null

    @Test
    fun test() {
        val orchaProgram = OrchaProgram()
        //transpiler.transpile(orchaProgram);
    }

}
