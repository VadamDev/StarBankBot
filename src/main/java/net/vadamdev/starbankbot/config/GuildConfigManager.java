package net.vadamdev.starbankbot.config;

import net.dv8tion.jda.api.entities.Guild;
import net.vadamdev.jdautils.configuration.ConfigurationLoader;
import net.vadamdev.starbankbot.utils.Utils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VadamDev
 * @since 31/12/2023
 */
public class GuildConfigManager {
    private final Logger logger;
    private final File directory;
    private final Map<String, GuildConfiguration> configs;

    public GuildConfigManager(Logger logger, File directory) {
        this.logger = logger;
        this.directory = directory;
        this.configs = new HashMap<>();

        logger.info("Looking for guild configurations...");

        for (File file : directory.listFiles()) {
            try {
                final GuildConfiguration config = new GuildConfiguration(file);
                ConfigurationLoader.loadConfiguration(config);
                configs.put(Utils.stripExtension(file.getName()), config);
            }catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        logger.info("-> Loaded " + configs.size() + " configurations !");
    }

    public GuildConfiguration getOrDefault(String guildId) {
        return configs.computeIfAbsent(guildId, k -> {
            try {
                final GuildConfiguration config = new GuildConfiguration(new File(directory.getPath(),guildId + ".yml"));
                ConfigurationLoader.loadConfiguration(config);
                return config;
            } catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public GuildConfiguration getOrDefault(Guild guild) {
        return getOrDefault(guild.getId());
    }

    public void deleteIfPresent(String guildId) {
        if(!configs.containsKey(guildId))
            return;

        try {
            configs.get(guildId).getYamlFile().deleteFile();
            configs.remove(guildId);
        }catch (IOException e) {
            logger.error("Attempted to deleted the config of " + guildId + " but an error occurred:");
            e.printStackTrace();
        }
    }

    public void deleteIfPresent(Guild guild) {
        deleteIfPresent(guild.getId());
    }
}
