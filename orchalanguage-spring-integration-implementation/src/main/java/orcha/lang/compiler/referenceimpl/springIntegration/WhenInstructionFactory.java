package orcha.lang.compiler.referenceimpl.springIntegration;

import orcha.lang.compiler.WhenInstruction;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class WhenInstructionFactory extends AbstractFactoryBean<WhenInstructionForSpringIntegration> {

    public WhenInstructionFactory(){
        setSingleton(false);
    }

    @Override
    public Class<?> getObjectType() {
        return WhenInstructionForSpringIntegration.class;
    }

    @Override
    protected WhenInstructionForSpringIntegration createInstance() throws Exception {
        return new WhenInstructionForSpringIntegration();
    }
}
