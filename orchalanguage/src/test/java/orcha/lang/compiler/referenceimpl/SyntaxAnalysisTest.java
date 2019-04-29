package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionForSpringIntegration;
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
public class SyntaxAnalysisTest {
	
	@Autowired
	SyntaxAnalysis syntaxAnalysisForTest;

	@Test
	public void contextLoads() {
	}

	@Test
	public void title() {

		String expression = null;

		try {

			expression = "title: un titre";
			TitleInstruction titleInstruction = new TitleInstruction(expression);
			titleInstruction.analysis();

			String titre = titleInstruction.getTitle();
			Assert.assertNotNull(titre);
			Assert.assertEquals(titre, "un titre");

		} catch (OrchaCompilationException e) {
			Assert.fail("Syntax error in: " + expression);
		}

		try {

			expression = "title 	 :	  un titre	 ";
			TitleInstruction titleInstruction = new TitleInstruction(expression);
			titleInstruction.analysis();

			String titre = titleInstruction.getTitle();
			Assert.assertNotNull(titre);
			Assert.assertEquals(titre, "un titre");

		} catch (OrchaCompilationException e) {
			Assert.fail("Syntax error in: " + expression);
		}

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

    @Test
	public void when() {

		String expression = null;

		try {

			expression = "	 when \"(event receives) and (one	 terminates  condition 	==false)\"";
			WhenInstruction whenExpression = new WhenInstructionForSpringIntegration(expression);
			whenExpression.analysis();
			List<WhenInstruction.ApplicationOrEventInExpression> applicationOrEvents = whenExpression.getApplicationsOrEvents();
			Assert.assertNotNull(applicationOrEvents);
			Assert.assertEquals(applicationOrEvents.size(), 2);

			WhenInstruction.ApplicationOrEventInExpression applicationOrEventInExpression = applicationOrEvents.get(0);
			String name = applicationOrEventInExpression.getName();
			Assert.assertNotNull(name);
			Assert.assertEquals(name, "event");
			WhenInstruction.State state = applicationOrEventInExpression.getState();
			Assert.assertNotNull(state);
			Assert.assertEquals(state, WhenInstruction.State.RECEIVES);
			int order = applicationOrEventInExpression.getOrder();
			Assert.assertEquals(order, 1);
			String condition = applicationOrEventInExpression.getCondition();
			Assert.assertNull(condition);

			applicationOrEventInExpression = applicationOrEvents.get(1);
			name = applicationOrEventInExpression.getName();
			Assert.assertNotNull(name);
			Assert.assertEquals(name, "one");
			state = applicationOrEventInExpression.getState();
			Assert.assertNotNull(state);
			Assert.assertEquals(state, WhenInstruction.State.TERMINATES);
			order = applicationOrEventInExpression.getOrder();
			Assert.assertEquals(order, 2);
			condition = applicationOrEventInExpression.getCondition();
			Assert.assertNotNull(condition);
			Assert.assertEquals(condition, "==false");

			String aggregationExpression = whenExpression.getAggregationExpression();
			Assert.assertNotNull(aggregationExpression);
			Assert.assertEquals(aggregationExpression, "size()==2 AND ((([0].payload instanceof Transpiler(orcha.lang.configuration.EventHandler))) AND (([1].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [1].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)))");


            expression = "when \"(one	 terminates  condition 	==false)\"";
            whenExpression = new WhenInstruction(expression);
            whenExpression.analysis();
            applicationOrEvents = whenExpression.getApplicationsOrEvents();
            Assert.assertNotNull(applicationOrEvents);
            Assert.assertEquals(applicationOrEvents.size(), 1);

            applicationOrEventInExpression = applicationOrEvents.get(0);
            name = applicationOrEventInExpression.getName();
            Assert.assertNotNull(name);
            Assert.assertEquals(name, "one");
            state = applicationOrEventInExpression.getState();
            Assert.assertNotNull(state);
            Assert.assertEquals(state, WhenInstruction.State.TERMINATES);
            order = applicationOrEventInExpression.getOrder();
            Assert.assertEquals(order, 1);
            condition = applicationOrEventInExpression.getCondition();
            Assert.assertNotNull(condition);
            Assert.assertEquals(condition, "==false");



            expression = "when \"one	 terminates  condition 	==true\"";
            whenExpression = new WhenInstruction(expression);
            whenExpression.analysis();
            applicationOrEvents = whenExpression.getApplicationsOrEvents();
            Assert.assertNotNull(applicationOrEvents);
            Assert.assertEquals(applicationOrEvents.size(), 1);

            applicationOrEventInExpression = applicationOrEvents.get(0);
            name = applicationOrEventInExpression.getName();
            Assert.assertNotNull(name);
            Assert.assertEquals(name, "one");
            state = applicationOrEventInExpression.getState();
            Assert.assertNotNull(state);
            Assert.assertEquals(state, WhenInstruction.State.TERMINATES);
            order = applicationOrEventInExpression.getOrder();
            Assert.assertEquals(order, 1);
            condition = applicationOrEventInExpression.getCondition();
            Assert.assertNotNull(condition);
            Assert.assertEquals(condition, "==true");



            expression = " when  \"  one	fails   \"";
            whenExpression = new WhenInstruction(expression);
            whenExpression.analysis();
            applicationOrEvents = whenExpression.getApplicationsOrEvents();
            Assert.assertNotNull(applicationOrEvents);
            Assert.assertEquals(applicationOrEvents.size(), 1);

            applicationOrEventInExpression = applicationOrEvents.get(0);
            name = applicationOrEventInExpression.getName();
            Assert.assertNotNull(name);
            Assert.assertEquals(name, "one");
            state = applicationOrEventInExpression.getState();
            Assert.assertNotNull(state);
            Assert.assertEquals(state, WhenInstruction.State.FAILS);



            expression = "when \" ( ( one	 terminates  condition 	==false ) and ( two terminates condition ==true ) ) or ( three terminates ) \"";
            whenExpression = new WhenInstructionForSpringIntegration(expression);
            whenExpression.analysis();
            aggregationExpression = whenExpression.getAggregationExpression();
            Assert.assertNotNull(aggregationExpression);
            Assert.assertEquals(aggregationExpression,"size()==3 AND (((([0].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)) AND (([1].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [1].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))) OR (([2].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [2].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)))");

        } catch (Exception e) {
			Assert.fail("Syntax error in: " + expression);
		}
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

	@Test(expected = OrchaCompilationException.class)
	public void sendError() throws OrchaCompilationException {
        String expression = "send 	data";
        SendInstruction sendInstruction = new SendInstruction(expression);
        sendInstruction.analysis();
    }

	/**
	 * receive event from ...		// line 1
	 * compute ... with event.value	// line 2
	 */

/*	@Test
	public void testReceiveBeforeCompute() {
		
		List<IntegrationNode> nodes = new ArrayList<IntegrationNode>();
		
		Instruction instruction = new Instruction();
		instruction.setCommand(Instruction.Command.RECEIVE);
		instruction.setVariable("event");
		instruction.setLineNumber(1);
		IntegrationNode receiveNode = new IntegrationNode();
		receiveNode.setInstruction(instruction);
		nodes.add(receiveNode);
		
		instruction = new Instruction();
		instruction.setCommand(Instruction.Command.COMPUTE);
		instruction.setLineNumber(2);
		
		List<Instruction.With> withs = new ArrayList<Instruction.With>();
		With with = instruction.new With();
		with.setWith("event");
		with.setWithProperty("value");
		instruction.setWiths(withs);
		
		IntegrationNode computeNode = new IntegrationNode();
		computeNode.setInstruction(instruction);
		nodes.add(computeNode);
		
		CompilerImpl.processIntructionsWithConsumer(
			nodes,
			inode -> inode.getInstruction().getCommand() == Instruction.Command.RECEIVE,
			inode -> inode.setNext(computeNode)
		);
		
		Assert.assertTrue(receiveNode.getNext() == computeNode);

	}*/

	/**
	 * receive event from ...		// line 1
	 * send event.value to ...		// line 2
	 */
/*	@Test
	public void testReceiveBeforeSend() {
		
		List<IntegrationNode> nodes = new ArrayList<IntegrationNode>();
		
		Instruction instruction = new Instruction();
		instruction.setCommand(Instruction.Command.RECEIVE);
		instruction.setVariable("event");
		instruction.setLineNumber(1);
		IntegrationNode receiveNode = new IntegrationNode();
		receiveNode.setInstruction(instruction);
		nodes.add(receiveNode);
		
		instruction = new Instruction();
		instruction.setCommand(Instruction.Command.SEND);
		instruction.setLineNumber(2);
		instruction.setVariable("event");
		instruction.setVariableProperty("value");
		
		IntegrationNode sendNode = new IntegrationNode();
		sendNode.setInstruction(instruction);
		nodes.add(sendNode);
		
		CompilerImpl.processIntructionsWithConsumer(
			nodes,
			inode -> inode.getInstruction().getCommand() == Instruction.Command.RECEIVE,
			inode -> inode.setNext(sendNode)
		);
		
		Assert.assertTrue(receiveNode.getNext() == sendNode);

	}

*/
	/**
	 * compute service with ...		// line 1
	 * when "service terminates" 	// line 2
	 */
	@Test
	public void testComputeBeforeWhen() {
		
	}

	@Test
	public void testCompute() {
		
/*		List<IntegrationNode> nodes = new ArrayList<IntegrationNode>();
		
		Instruction instruction = new Instruction();
		instruction.setCommand("receive");
		IntegrationNode receiveNode = new IntegrationNode();
		receiveNode.setInstruction(instruction);
		nodes.add(receiveNode);
		
		instruction = new Instruction();
		instruction.setCommand("compute");
		IntegrationNode computeNode = new IntegrationNode();
		computeNode.setInstruction(instruction);
		nodes.add(computeNode);
		
		CompilerImpl.processElements(
			nodes,
			inode -> inode.getInstruction().getCommand() == "receive",
			inode -> inode.getInstruction().getVariable(),
			variable -> System.out.println(variable)
		);
		
		//Assert.assertTrue(receiveNode.getNext() == computeNode);
*/
	}

}

