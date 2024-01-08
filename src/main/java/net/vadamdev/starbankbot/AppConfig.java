package net.vadamdev.starbankbot;

import net.vadamdev.jdautils.configuration.ConfigValue;
import net.vadamdev.jdautils.configuration.Configuration;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public class AppConfig extends Configuration {
    @ConfigValue(path = "token")
    public String token = "BOT_TOKEN";

    protected AppConfig() {
        super("./config.yml");
    }
}
