package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.syntax.SendInstruction;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class SendInstructionFactory extends AbstractFactoryBean<SendInstruction> {

    public SendInstructionFactory(){
        setSingleton(false);
    }

    @Override
    public Class<?> getObjectType() {
        return SendInstruction.class;
    }

    @Override
    protected SendInstruction createInstance() throws Exception {
        return new SendInstruction();
    }
}
