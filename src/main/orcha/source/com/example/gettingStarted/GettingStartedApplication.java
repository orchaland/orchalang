package com.example.gettingStarted;

import java.io.File;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.MessageChannel;

public class GettingStartedApplication {

    @Bean
    public IntegrationFlow generation() {
        return IntegrationFlows.from(Files.inboundAdapter(new File("./files")).patternFilter("*.json"), a -> a.poller(Pollers.fixedDelay(1000))).transform(Files.toStringTransformer()).channel("processFileChannel").get();
    }

    @Bean
    public MessageChannel processFileChannel() {
        return new DirectChannel();
    }
}
