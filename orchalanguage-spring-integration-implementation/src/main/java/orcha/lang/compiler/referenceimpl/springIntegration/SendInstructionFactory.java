package orcha.lang.compiler.referenceimpl.springIntegration;

import org.springframework.beans.factory.config.AbstractFactoryBean;

public class SendInstructionFactory extends AbstractFactoryBean<SendInstructionForSpringIntegration> {

    public SendInstructionFactory(){
        setSingleton(false);
    }

    @Override
    public Class<?> getObjectType() {
        return SendInstructionForSpringIntegration.class;
    }

    @Override
    protected SendInstructionForSpringIntegration createInstance() throws Exception {
        return new SendInstructionForSpringIntegration();
    }
}
