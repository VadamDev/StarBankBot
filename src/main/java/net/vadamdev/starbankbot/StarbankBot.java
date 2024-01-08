package net.vadamdev.starbankbot;

import net.vadamdev.jdautils.application.JDABot;
import net.vadamdev.starbankbot.commands.AUECCommand;
import net.vadamdev.starbankbot.commands.settings.SettingsCommand;
import net.vadamdev.starbankbot.config.GuildConfigManager;
import net.vadamdev.starbankbot.language.LanguageManager;
import net.vadamdev.starbankbot.listeners.EventListener;
import net.vadamdev.starbankbot.transaction.TransactionManager;
import net.vadamdev.starbankbot.utils.Utils;
import org.slf4j.Logger;

import java.io.File;

/**
 * @author VadamDev
 * @since 30/12/2023
 */
public class StarbankBot extends JDABot {
    private final File guildsConfigDirectory;

    private LanguageManager languageManager;
    private GuildConfigManager guildConfigManager;
    private TransactionManager transactionManager;

    public StarbankBot() {
        super(BotToken.RELEASE.getToken(), null);

        this.guildsConfigDirectory = Utils.initDirectory("./guilds");
    }

    @Override
    public void onEnable() {
        final Logger logger = Main.logger;

        languageManager = new LanguageManager(logger);
        guildConfigManager = new GuildConfigManager(logger, guildsConfigDirectory);
        transactionManager = new TransactionManager();

        registerListeners(new EventListener());

        registerCommands(
                new SettingsCommand(),

                new AUECCommand()
        );
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public GuildConfigManager getGuildConfigManager() {
        return guildConfigManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}
