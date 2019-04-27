package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.springIntegration.SpringIntegrationAutoConfiguration;
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionFactory;
import orcha.lang.compiler.referenceimpl.springIntegration.WhenInstructionForSpringIntegration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SpringIntegrationAutoConfiguration.class, CompilerReferenceImplTestConfiguration.class})
public class SyntaxAnalysisTest {
	
	@Autowired
	SyntaxAnalysis syntaxAnalysisForTest;

    @Resource(name = "&whenInstruction")
    WhenInstructionFactory whenInstructionFactory;

    @Test
	public void when() {

		String expression = null;

		try {

			expression = "	 when \"(event receives) and (one	 terminates  condition 	==false)\"";
			//WhenInstruction whenExpression = new WhenInstructionForSpringIntegration(expression);
            WhenInstruction whenExpression = whenInstructionFactory.getObject();
            whenExpression.setInstruction(expression);
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
            //whenExpression = new WhenInstructionForSpringIntegration(expression);
            whenExpression = whenInstructionFactory.getObject();
            whenExpression.setInstruction(expression);
            whenExpression.analysis();
            aggregationExpression = whenExpression.getAggregationExpression();
            Assert.assertNotNull(aggregationExpression);
            Assert.assertEquals(aggregationExpression,"size()==3 AND (((([0].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [0].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)) AND (([1].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [1].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED))) OR (([2].payload instanceof Transpiler(orcha.lang.configuration.Application) AND [2].payload.state==Transpiler(orcha.lang.configuration.State).TERMINATED)))");

        } catch (Exception e) {
			Assert.fail("Syntax error in: " + expression);
		}
	}

}

