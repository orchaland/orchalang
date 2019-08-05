package configuration.transport;

import orcha.lang.configuration.*;
import org.springframework.context.annotation.Bean;
import service.transport.ProfileUser;

public class ProductOrderConfiguration {

    @Bean
    public EventHandler user(){
        EventHandler eventHandler = new EventHandler("user");
        InputFileAdapter inputFileAdapter = new InputFileAdapter("data/input", "request.json");
        eventHandler.setInput(new Input("application/json", "service.transport.Request", inputFileAdapter));
        OutputFileAdapter outputFileAdapter = new OutputFileAdapter("data/output", true, "securityAlert.json", true, OutputFileAdapter.WritingMode.REPLACE);
        eventHandler.setOutput(new Output("application/json", "service.airport.SecurityAlert", outputFileAdapter));
        return eventHandler;
    }

    @Bean
    public ProfileUser profileUser() {
        return new ProfileUser();
    }

    @Bean
    public Application authenticateUsersRequest(){
        Application application = new Application("authenticateUsersRequest", "Java");
        JavaServiceAdapter javaAdapter = new JavaServiceAdapter("service.transport.ProfileUser", "check");
        application.setInput(new Input("service.transport.Request", javaAdapter));
        application.setOutput(new Output("service.transport.Request", javaAdapter));
        return application;
    }

}
