package net.vadamdev.jdautils.message;

import net.dv8tion.jda.api.entities.Guild;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public interface SmartMessageProvider {
    void init(Guild guild, MessageContent contents);

    void populate(Guild guild, MessageContent contents);
}
