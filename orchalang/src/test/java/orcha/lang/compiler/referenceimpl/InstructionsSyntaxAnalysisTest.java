package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import orcha.lang.compiler.syntax.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InstructionsSyntaxAnalysisTest {
	
	@Autowired
	SyntaxAnalysis syntaxAnalysisForTest;

	@Test
	public void contextLoads() {
	}

	@Test
	public void receive() {

		String expression = null;

		try{
			expression = "receive order from customer condition name=\"Ben\"";
			ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
			receiveInstruction.analysis();

			String event = receiveInstruction.getEvent();
			Assert.assertNotNull(event);
			Assert.assertEquals(event, "order");

			String source = receiveInstruction.getSource();
			Assert.assertNotNull(source);
			Assert.assertEquals(source, "customer");

			String condition = receiveInstruction.getCondition();
			Assert.assertNotNull(condition);
			Assert.assertEquals(condition, "name=\"Ben\"");
		}catch (OrchaCompilationException e){
			Assert.fail("Syntax error in: " + expression);
		}

		try{
			expression = "receive order from customer";
			ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
			receiveInstruction.analysis();

			String event = receiveInstruction.getEvent();
			Assert.assertNotNull(event);
			Assert.assertEquals(event, "order");

			String source = receiveInstruction.getSource();
			Assert.assertNotNull(source);
			Assert.assertEquals(source, "customer");

			String condition = receiveInstruction.getCondition();
			Assert.assertNull(condition);
		}catch (OrchaCompilationException e){
			Assert.fail("Syntax error in: " + expression);
		}

		try{
			expression = " 	receive order 	from  	customer 	";
			ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
			receiveInstruction.analysis();

			String event = receiveInstruction.getEvent();
			Assert.assertNotNull(event);
			Assert.assertEquals(event, "order");

			String source = receiveInstruction.getSource();
			Assert.assertNotNull(source);
			Assert.assertEquals(source, "customer");

			String condition = receiveInstruction.getCondition();
			Assert.assertNull(condition);
		}catch (OrchaCompilationException e){
			Assert.fail("Syntax error in: " + expression);
		}

	}

    @Test(expected = OrchaCompilationException.class)
    public void receiveError() throws OrchaCompilationException {
        String expression = " 	reive order 	from  	customer 	";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test(expected = OrchaCompilationException.class)
    public void receiveError1() throws OrchaCompilationException {
        String expression = " 	receive order 	from";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test(expected = OrchaCompilationException.class)
    public void receiveError2() throws OrchaCompilationException {
        String expression = " 	receive from";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test(expected = OrchaCompilationException.class)
    public void receiveError3() throws OrchaCompilationException {
        String expression = " 	receive order 	to  	customer 	";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test
	public void compute() {

		String expression = null;

		try {
			expression = "compute checkOrder";
			ComputeInstruction computeInstruction = new ComputeInstruction(expression);
			computeInstruction.analysis();

			String application = computeInstruction.getApplication();
			Assert.assertNotNull(application);
			Assert.assertEquals(application, "checkOrder");

			List<String> parameters = computeInstruction.getParameters();
			Assert.assertNotNull(parameters);
			Assert.assertEquals(parameters.size(), 0);

		} catch (Exception e) {
			Assert.fail("Syntax error in: " + expression);
		}

		try {
			expression = "	 compute 	checkOrder 	 with 	order,     account ";
			ComputeInstruction computeInstruction = new ComputeInstruction(expression);
			computeInstruction.analysis();

			String application = computeInstruction.getApplication();
			Assert.assertNotNull(application);
			Assert.assertEquals(application, "checkOrder");

			List<String> parameters = computeInstruction.getParameters();
			Assert.assertNotNull(parameters);
			Assert.assertEquals(parameters.size(), 2);

			Assert.assertEquals(parameters.get(0), "order");
			Assert.assertEquals(parameters.get(1), "account");

		} catch (Exception e) {
			Assert.fail("Syntax error in: " + expression);
		}
	}

    @Test(expected = OrchaCompilationException.class)
    public void computeError() throws OrchaCompilationException {
        String expression = "checkOrder with order";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test(expected = OrchaCompilationException.class)
    public void computeError1() throws OrchaCompilationException {
        String expression = "compute checkOrder with";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test(expected = OrchaCompilationException.class)
    public void whenError() throws OrchaCompilationException {
        String expression = "when \"one	 tenates  condition 	==false\"";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test(expected = OrchaCompilationException.class)
    public void whenError1() throws OrchaCompilationException {
        String expression = "when \"one	 terminates  condition\"";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test(expected = OrchaCompilationException.class)
    public void whenError2() throws OrchaCompilationException {
        String expression = "when \"one	 terminates  condition 	==false\"";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test(expected = OrchaCompilationException.class)
    public void whenError3() throws OrchaCompilationException {
        String expression = "when one	 terminates  condition 	==false";
        ReceiveInstruction receiveInstruction = new ReceiveInstruction(expression);
        receiveInstruction.analysis();
    }

    @Test
	public void send() {

        String expression = "	send 	data  to 	customer	,  provider ";

        try {

            SendInstruction sendInstruction = new SendInstruction(expression);
            sendInstruction.analysis();
            String data = sendInstruction.getData();
            Assert.assertNotNull(data);
            Assert.assertEquals(data, "data");
            List<String> destinations = sendInstruction.getDestinations();
            Assert.assertNotNull(destinations);
            Assert.assertEquals(destinations.size(), 2);
            String destination = destinations.get(0);
            Assert.assertEquals(destination, "customer");
            destination = destinations.get(1);
            Assert.assertEquals(destination, "provider");

		} catch (Exception e) {
            Assert.fail("Syntax error in: " + expression);
		}
	}

	@Test
	public void sendWithVariable(){
		String expression = "send order.number to destination";
		try{

			SendInstruction sendIntruction = new SendInstruction(expression);
			sendIntruction.analysis();
			String variables = sendIntruction.getVariables();
			Assert.assertNotNull(variables);
			Assert.assertEquals(variables, "number");

		} catch (Exception e) {
			Assert.fail("Syntax error in: " + expression);
		}

		expression = "send order.product.reference to destination";
		try{

			SendInstruction sendIntruction = new SendInstruction(expression);
			sendIntruction.analysis();
			String variables = sendIntruction.getVariables();
			Assert.assertNotNull(variables);
			Assert.assertEquals(variables, "product.reference");

		} catch (Exception e) {
			Assert.fail("Syntax error in: " + expression);
		}

	}


	@Test(expected = OrchaCompilationException.class)
	public void sendError() throws OrchaCompilationException {
        String expression = "send 	data";
        SendInstruction sendInstruction = new SendInstruction(expression);
        sendInstruction.analysis();
    }


}

