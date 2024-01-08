package net.vadamdev.starbankbot;

import net.vadamdev.jdautils.application.JDAApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(StarbankBot.class);
    public static final StarbankBot starbankBot = new StarbankBot();

    public static void main(String[] args) {
        final JDAApplication<StarbankBot> application = new JDAApplication<>(starbankBot, logger);

        //TODO: remove debug
        application.registerCommand("ram", bot -> {
            final Runtime runtime = Runtime.getRuntime();

            final long total = runtime.totalMemory() / 1048576;
            final long used = total - runtime.freeMemory() / 1048576;

            System.out.println("Total: " + total + " MB");
            System.out.println("Used: " + used + " MB");
        });

        application.start();
    }
}