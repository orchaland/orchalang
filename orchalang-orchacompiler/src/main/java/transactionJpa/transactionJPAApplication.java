package transactionJpa;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.*;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.integration.jpa.support.PersistMode;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@SpringBootApplication
@IntegrationComponentScan
public class transactionJPAApplication {

	@Bean(name = "preprocessingForOrchaCompiler")
	Preprocessing preprocessing(){
		return new PreprocessingImpl();
	}

	@Bean(name="syntaxAnalysisForOrchaCompiler")
	public SyntaxAnalysis syntaxAnalysis() {
		return new SyntaxAnalysisImpl();
	}

	@Bean(name="semanticAnalysisForOrchaCompiler")
	public SemanticAnalysis semanticAnalysis()  {
		return new SemanticAnalysisImpl();
	}

	@Bean(name="postprocessingForOrchaCompiler")
	public Postprocessing postprocessing() {
		return new PostprocessingImpl();
	}

	@Bean(name="lexicalAnalysisForOrchaCompiler")
	public LexicalAnalysis lexicalAnalysis() {
		return new LexicalAnalysisImpl();
	}

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Bean
	public IntegrationFlow pollingAdapterFlow() {
		return IntegrationFlows
				.from(Jpa.inboundAdapter(this.entityManagerFactory)
								.entityClass(StudentDomain.class)
								.maxResults(1)
								.expectSingleResult(true),
						e -> e.poller(p -> p.fixedDelay(5000)))
				//.handle("PopulateDatabase", "enrollStudent")
				.channel("enrollStudentChannel.input")
				.log()
				//.channel(c -> c.queue("pollingResults"))
				.get();
	}

	@Bean
	public IntegrationFlow enrollStudentChannel() {
		return f -> f
				.handle("PopulateDatabase", "enrollStudent", c -> c.transactional(true))
				//.enrichHeaders(h -> h.header("sendChannel", "send_student_to_welcomeFile"))
				.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
				.routeToRecipients(r -> r
						.recipient("outputRetainingAggregatorChannel")
						.recipient("outputChannel")							// next channel
				);
	}
	@Bean
	public DirectChannel outputChannel() {
		return new DirectChannel();
	}
	@Bean
	public DirectChannel outputRetainingAggregatorChannel() {
		return new DirectChannel();
	}
	@Bean
	public IntegrationFlow outputRetainingAggregatorFlow() {
		return IntegrationFlows.from(outputRetainingAggregatorChannel())
				.aggregate(a ->	a
						.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )")
						.correlationExpression("headers['messageID']"))
				.get();
	}


	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(transactionJPAApplication.class, args);

		PopulateDatabase populateDatabase = (PopulateDatabase)context.getBean("populateDatabase");
		List<?> results = populateDatabase.readDatabase();
		try{

			StudentDomain student = new StudentDomain("Morgane", 21, 1);
			populateDatabase.saveStudent(student);
			StudentDomain student1 = new StudentDomain("Morgane1", 35, 2);
			populateDatabase.saveStudent(student1);
			//List<?> results = populateDatabase.readDatabase();
			System.out.println("database: " + results);

		} catch(Exception e){
			System.out.println(">>>>>> Caught exception: " + e);
		}
		results = populateDatabase.readDatabase();
		System.out.println("database: " + results);
	}
}
