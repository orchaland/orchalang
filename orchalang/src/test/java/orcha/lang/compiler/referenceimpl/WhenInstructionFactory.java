package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.syntax.WhenInstruction;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class WhenInstructionFactory extends AbstractFactoryBean<WhenInstruction> {

    public WhenInstructionFactory(){
        setSingleton(false);
    }

    @Override
    public Class<?> getObjectType() {
        return WhenInstruction.class;
    }

    @Override
    protected WhenInstruction createInstance() throws Exception {
        return new WhenInstruction();
    }
}
