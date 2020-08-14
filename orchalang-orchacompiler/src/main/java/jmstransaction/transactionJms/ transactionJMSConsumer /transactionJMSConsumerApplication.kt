package jmstransaction.transactionJms.` transactionJMSConsumer `

import orcha.lang.compiler.*
import orcha.lang.compiler.referenceimpl.*
import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.QueueChannel
import org.springframework.integration.dsl.*
import org.springframework.integration.file.dsl.Files
import org.springframework.integration.handler.ServiceActivatingHandler
import org.springframework.integration.jms.dsl.Jms
import org.springframework.integration.jms.dsl.JmsDefaultListenerContainerSpec
import org.springframework.jms.connection.JmsTransactionManager
import org.springframework.messaging.PollableChannel
import java.io.File
import javax.jms.ConnectionFactory


@SpringBootApplication
@Configuration
@ImportAutoConfiguration(ActiveMQAutoConfiguration::class, JmxAutoConfiguration::class, IntegrationAutoConfiguration::class)
@IntegrationComponentScan
@ComponentScan
class transactionJMSConsumerApplication : CommandLineRunner {

    @Bean
    fun conditionalService(): ConditionalService {
        return ConditionalService()
    }

    @Value("\${activemq.broker-url}")
    var brokerURL: String? = null

    @Value("\${activemq.user}")
    var brokerUserName: String? = null

    @Value("\${activemq.password}")
    var brokerPassword: String? = null

    @Value("\${jms.inbound.poller.fixedDelay:500}")
    var fixedDelay: Long = 0

    @Bean
    fun jmsConnectionFactory(): ConnectionFactory {
        val activeMQConnectionFactory = ActiveMQConnectionFactory()
        activeMQConnectionFactory.brokerURL = brokerURL
        activeMQConnectionFactory.userName = brokerUserName
        activeMQConnectionFactory.password = brokerPassword
        return ActiveMQConnectionFactory()
    }

    @Autowired
    var jmsConnectionFactory: ConnectionFactory? = null

    @Bean
    fun jmsTransactionManager(): JmsTransactionManager {
        val jmsTransactionManager = JmsTransactionManager()
        jmsTransactionManager.connectionFactory = jmsConnectionFactory
        return jmsTransactionManager
    }

    @Bean
    fun jmsInboundGatewayFlow(): IntegrationFlow {
        return IntegrationFlows.from(Jms.inboundGateway(jmsConnectionFactory)
                .destination("jmsPipelineTest")
                .configureListenerContainer { spec: JmsDefaultListenerContainerSpec ->
                    spec
                            .pubSubDomain(true) // true for topic, else queue
                            .clientId("MarwaID1") // for durable subscription
                            .subscriptionDurable(true)
                            .durableSubscriptionName("durableSubscriptionName")
                }
                .requestChannel(jmsInboundChannel()) //.replyDeliveryPersistent(true)
                //.replyTimeToLive(-1)
                .replyChannel(jmsReplyChannel1())
                .extractRequestPayload(true)
                .extractReplyPayload(true)) //.errorChannel(jmsErrorChannel()))
                .get()
    }

    @Bean
    fun jmsInboundChannel(): PollableChannel {
        return QueueChannel()
    }

    @Bean
    fun jmsReplyChannel1(): DirectChannel {
        return DirectChannel()
    }

    @Bean
    fun jmsErrorChannel(): DirectChannel {
        return DirectChannel()
    }

    @Bean //@Transactional(value="jmsTransactionManager", propagation = Propagation.NESTED)
    fun fileWritingFlow(): IntegrationFlow {
        return IntegrationFlows.from(jmsInboundChannel())
                .handle("conditionalService", "messageReceived") { e: GenericEndpointSpec<ServiceActivatingHandler?> ->
                    e
                            .poller(Pollers.fixedDelay(fixedDelay)
                                    .maxMessagesPerPoll(1)
                                    .transactional(jmsTransactionManager()))
                } // The entire flow is transactional (for example, if there is a downstream outbound channel adapter)
                .routeToRecipients { r: RecipientListRouterSpec ->
                    r
                            .recipient(jmsReplyChannel1())
                            .recipient(fileWritingChannel1())
                            .recipient(fileWritingChannel2())
                }
                .get()
    }

    @Bean
    fun fileWritingChannel1(): DirectChannel {
        return DirectChannel()
    }

    @Bean //@Transactional(value="jmsTransactionManager", propagation = Propagation.NESTED)
    fun fileWritingFlow1(): IntegrationFlow {
        return IntegrationFlows.from(fileWritingChannel1())
                .enrichHeaders { h: HeaderEnricherSpec -> h.header("directory", File("./output1")) }
                .handle(Files.outboundGateway(File("./output1")).autoCreateDirectory(true))
                .handle("conditionalService", "fileWriting1")
                .log()
                .get()
    }

    @Bean
    fun fileWritingChannel2(): DirectChannel {
        return DirectChannel()
    }

    @Bean //@Transactional(value="jmsTransactionManager", propagation = Propagation.NESTED)
    fun fileWritingFlow2(): IntegrationFlow {
        return IntegrationFlows.from(fileWritingChannel2())
                .handle("conditionalService", "fileWriting2")
                .enrichHeaders { h: HeaderEnricherSpec -> h.header("directory", File("./output2")) }
                .handle(Files.outboundAdapter(File("./output2")).autoCreateDirectory(true))
                .get()
    }

    @Throws(Exception::class)
    override fun run(vararg args: String) {
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(transactionJMSConsumerApplication::class.java, *args)
        }
    }
}