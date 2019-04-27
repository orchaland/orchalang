import me.Me;
import me.MeConfig;
import me.MeProperties;
import orcha.lang.compiler.OrchaMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static me.MeConfigKey.*;

@Configuration
@ConditionalOnClass(Me.class)
@EnableConfigurationProperties(MeProperties.class)
public class MeAutoConfiguration {

    @Autowired
    private MeProperties meProperties;

    OrchaMetadata     OrchaMetadata;


    @Bean
    @ConditionalOnMissingBean
    public Me myInfo(MeConfig meConfig) {
        return new Me(meConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public MeConfig meConfig() {
        String name = meProperties.getName() == null ? System.getProperty("info.name") : meProperties.getName();
        String age = meProperties.getAge();
        String gender = meProperties.getGender() == null ? "male" : meProperties.getGender();
        String address = meProperties.getAddress();

        MeConfig meConfig = new MeConfig();
        meConfig.put(NAME, name);
        if (age != null){
            meConfig.put(AGE, age);
        }
        meConfig.put(GENDER, gender);
        if (address != null){
            meConfig.put(ADDRESS, address);
        }
        return meConfig;
    }
}
