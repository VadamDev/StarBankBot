package net.vadamdev.starbankbot.config;

import net.vadamdev.jdautils.configuration.ConfigValue;
import net.vadamdev.jdautils.configuration.Configuration;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.language.Lang;
import net.vadamdev.starbankbot.transaction.DistributionMode;

import java.io.File;

/**
 * @author VadamDev
 * @since 30/12/2023
 */
public class GuildConfiguration extends Configuration {
    @ConfigValue(path = "lang")
    public String LANG = Lang.EN_US.name();

    @ConfigValue(path = "transaction.mode")
    public String TRANSACTION_MODE = DistributionMode.DEFAULT.name();

    @ConfigValue(path = "transaction.percentage")
    public int TRANSACTION_PERCENTAGE = 10;

    @ConfigValue(path = "transaction.allowOverride")
    public boolean TRANSACTION_ALLOW_OVERRIDE = false;

    public GuildConfiguration(File file) {
        super(file);
    }

    public GuildConfiguration(String path) {
        super(path);
    }

    public Lang getLang() {
        try {
            return Lang.valueOf(LANG);
        }catch(Exception ignored) {
            Main.logger.warn("getLang() returned a fallback value this should not happen");
            return Lang.EN_US;
        }
    }

    public DistributionMode getDistributionMode() {
        try {
            return DistributionMode.valueOf(TRANSACTION_MODE);
        }catch(Exception ignored) {
            Main.logger.warn("getDistributionMode() returned a fallback value this should not happen");
            return DistributionMode.DEFAULT;
        }
    }
}
