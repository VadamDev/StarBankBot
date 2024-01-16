package net.vadamdev.starbankbot;

import net.vadamdev.jdautils.application.JDAApplication;
import net.vadamdev.jdautils.configuration.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(StarbankBot.class);
    public static final StarbankBot starbankBot;

    static {
        final AppConfig config = new AppConfig();

        try {
            final boolean existedBefore = config.getYamlFile().exists();
            ConfigurationLoader.loadConfiguration(config);

            if(!existedBefore) {
                logger.info("Generated default app configuration, please put the bot token in the config.yml file");
                System.exit(0);
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        starbankBot = new StarbankBot(config);
    }

    public static void main(String[] args) {
        final JDAApplication<StarbankBot> application = new JDAApplication<>(starbankBot, logger);
        application.start();
    }
}