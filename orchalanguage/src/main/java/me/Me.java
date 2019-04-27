package me;

import static me.MeConfigKey.*;

public class Me {

    private MeConfig meConfig;

    public Me(MeConfig myInfoConfig) {
        this.meConfig = myInfoConfig;
    }

    public String print() {
        String name = meConfig.getProperty(NAME);
        String age = meConfig.getProperty(AGE);
        String gender = meConfig.getProperty(GENDER);
        String address = meConfig.getProperty(ADDRESS);

        StringBuilder builder = new StringBuilder();
        String nameInfo = String.format("This is %s %s", gender.equalsIgnoreCase("male") ? "MR" : "MS", name);
        builder.append(nameInfo);
        if (age != null) {
            builder.append(String.format(", I'm %s years old", age));
        }
        if (address != null) {
            builder.append(String.format("and I lived in %s now.", address));
        }
        return builder.toString();
    }
}
