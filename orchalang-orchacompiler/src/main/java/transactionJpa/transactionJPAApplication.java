package transactionJpa;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.*;
import orcha.lang.compiler.referenceimpl.springIntegration.ApplicationToMessage;
import orcha.lang.compiler.referenceimpl.springIntegration.MessageToApplication;
import orcha.lang.configuration.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
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

	/*
     receive student from database
     enrollStudent with student
     when " enrollStudent  terminates"
     send   enrollStudent.result to studentBase
     */

	
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
	public IntegrationFlow StudentbaseFlow() {
		return IntegrationFlows
				.from(Jpa.inboundAdapter(this.entityManagerFactory)
								.entityClass(StudentDomain.class)
								.maxResults(1)
								.expectSingleResult(true),
						e -> e.poller(p -> p.fixedDelay(5000)))
				.enrichHeaders(h -> h.headerExpression("messageID", "headers['id'].toString()"))
				.channel("enrollStudentChannel.input")
				.log()
				.get();
	}
	@Bean
	MessageToApplication enrollStudentMessageToApplication() {
		return new MessageToApplication(Application.State.TERMINATED, "enrollStudent");
	}

	@Bean
	ApplicationToMessage applicationToMessage(){
		return new ApplicationToMessage();
	}

	@Bean
	public IntegrationFlow enrollStudentChannel() {
		return f -> f
				.handle("EnrollStudent", "enrollStudent", c -> c.transactional(true))
				.handle(enrollStudentMessageToApplication(), "transform")
						.channel("outputenrollStudentggregatorChannel.input");

	}

	@Bean
	public IntegrationFlow outputenrollStudentggregatorChannel() {
		return f->f.aggregate(a ->	a
						.releaseExpression("size()==1 and ( ((getMessages().toArray())[0].payload instanceof T(orcha.lang.configuration.Application) AND (getMessages().toArray())[0].payload.state==T(orcha.lang.configuration.Application.State).TERMINATED) )")
						.correlationExpression("headers['messageID']"))
				.transform("payload.?[name=='enrollStudent']")
				.handle(applicationToMessage(), "transform")
				.channel("outputChannel.input");
	}

	@Bean
	public DirectChannel outputChannel() {
		return new DirectChannel();
	}


	@Bean
	public IntegrationFlow outboundStudentBaseFlow() {
		return IntegrationFlows.from(outputChannel())
				.handle(Jpa.outboundAdapter(this.entityManagerFactory)
								.entityClass(StudentDomain.class)
								.persistMode(PersistMode.PERSIST),
						c -> c.transactional(true))
				.get();
	}


	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(transactionJPAApplication.class, args);

		PopulateDatabase populateDatabase = (PopulateDatabase)context.getBean("populateDatabase");


		System.out.println("\nmanyStudentsInValideTransaction is starting\n");
		try{
			StudentDomain student = new StudentDomain("Morgane", 21, 1);
			populateDatabase.saveStudent(student);
			List<?> results = populateDatabase.readDatabase();
			System.out.println("database: " + results);

		} catch(Exception e){
			System.out.println(">>>>>> Caught exception: " + e);
		}


	}
}
