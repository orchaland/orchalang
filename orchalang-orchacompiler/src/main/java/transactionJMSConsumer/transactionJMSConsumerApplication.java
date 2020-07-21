package transactionJMSConsumer;

import orcha.lang.compiler.*;
import orcha.lang.compiler.referenceimpl.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.messaging.PollableChannel;

import javax.jms.ConnectionFactory;
import java.io.File;

@SpringBootApplication
@Configuration
@ImportAutoConfiguration({ ActiveMQAutoConfiguration.class, JmxAutoConfiguration.class, IntegrationAutoConfiguration.class })
@IntegrationComponentScan
@ComponentScan
public class transactionJMSConsumerApplication implements CommandLineRunner {
	@Bean(name="lexicalAnalysisForOrchaCompiler")
	public LexicalAnalysis lexicalAnalysis() {
		return new LexicalAnalysisImpl();
	}


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
	@Bean
	ConditionalService conditionalService(){
		return new ConditionalService();
	}

	@Value("${activemq.broker-url}")
	String brokerURL;
	@Value("${activemq.user}")
	String brokerUserName;
	@Value("${activemq.password}")
	String brokerPassword;

	@Value("${jms.inbound.poller.fixedDelay:500}")
	long fixedDelay;

	@Bean
	public ConnectionFactory jmsConnectionFactory(){
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		activeMQConnectionFactory.setBrokerURL(brokerURL);
		activeMQConnectionFactory.setUserName(brokerUserName);
		activeMQConnectionFactory.setPassword(brokerPassword);
		return new ActiveMQConnectionFactory();
	}

	@Autowired
	ConnectionFactory jmsConnectionFactory;

	@Bean
	public JmsTransactionManager jmsTransactionManager() {
		JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
		jmsTransactionManager.setConnectionFactory(this.jmsConnectionFactory);
		return jmsTransactionManager;
	}

	@Bean
	public IntegrationFlow jmsInboundGatewayFlow(){
		return IntegrationFlows.from(Jms.inboundGateway(this.jmsConnectionFactory)
				.destination("jmsPipelineTest")
				.configureListenerContainer(spec -> spec
								.pubSubDomain(true)				// true for topic, else queue
								.clientId("MarwaID1")			// for durable subscription
								.subscriptionDurable(true)
								.durableSubscriptionName("durableSubscriptionName")
						//.backOff() ?
				)
				.requestChannel(jmsInboundChannel())
				//.replyDeliveryPersistent(true)
				//.replyTimeToLive(-1)
				.replyChannel(jmsReplyChannel1())
				.extractRequestPayload(true)
				.extractReplyPayload(true))
				//.errorChannel(jmsErrorChannel()))
				.get();
	}

	@Bean
	public PollableChannel jmsInboundChannel() {
		return new QueueChannel();
	}

	@Bean
	public DirectChannel jmsReplyChannel1() {
		return new DirectChannel();
	}

	@Bean
	public DirectChannel jmsErrorChannel() {
		return new DirectChannel();
	}

	@Bean
	//@Transactional(value="jmsTransactionManager", propagation = Propagation.NESTED)
	public IntegrationFlow fileWritingFlow() {
		return IntegrationFlows.from(jmsInboundChannel())
				.handle("conditionalService", "messageReceived", e -> e
						.poller(Pollers.fixedDelay(fixedDelay)
								.maxMessagesPerPoll(1)
								.transactional(this.jmsTransactionManager()))) 	// The entire flow is transactional (for example, if there is a downstream outbound channel adapter)
				.routeToRecipients(r -> r
						.recipient(jmsReplyChannel1())
						.recipient(fileWritingChannel1())
						.recipient(fileWritingChannel2()))
				.get();
	}

	@Bean
	public DirectChannel fileWritingChannel1() {
		return new DirectChannel();
	}

	@Bean
	//@Transactional(value="jmsTransactionManager", propagation = Propagation.NESTED)
	public IntegrationFlow fileWritingFlow1() {
		return IntegrationFlows.from(fileWritingChannel1())
				.enrichHeaders(h -> h.header("directory", new File("./output1")))
				.handle(Files.outboundGateway(new File( "./output1")).autoCreateDirectory(true) )
				.handle("conditionalService", "fileWriting1")
				.log()
				.get();
	}

	@Bean
	public DirectChannel fileWritingChannel2() {
		return new DirectChannel();
	}

	@Bean
	//@Transactional(value="jmsTransactionManager", propagation = Propagation.NESTED)
	public IntegrationFlow fileWritingFlow2() {
		return IntegrationFlows.from(fileWritingChannel2())
				.handle("conditionalService", "fileWriting2")
				.enrichHeaders(h -> h.header("directory", new File("./output2")))
				.handle(Files.outboundAdapter(new File( "./output2")).autoCreateDirectory(true) )
				.get();
	}

	public static void main(String[] args) {

		SpringApplication.run(transactionJMSConsumerApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {


	}
}
