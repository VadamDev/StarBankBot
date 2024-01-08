package net.vadamdev.starbankbot.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.vadamdev.jdautils.commands.Command;
import net.vadamdev.jdautils.commands.ISlashCommand;
import net.vadamdev.jdautils.commands.data.ICommandData;
import net.vadamdev.jdautils.commands.data.SlashCmdData;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VadamDev
 * @since 30/12/2023
 */
public class SettingsCommand extends Command implements ISlashCommand {
    protected static final Permission SETTINGS_PERMISSION = Permission.ADMINISTRATOR;

    public static final Map<String, SettingsMessage> messagesMap = new HashMap<>();

    public SettingsCommand() {
        super("settings");
        setPermission(SETTINGS_PERMISSION);
    }

    @Override
    public void execute(@Nonnull Member sender, @Nonnull ICommandData commandData) {
        final SlashCommandInteractionEvent event = commandData.castOrNull(SlashCmdData.class).getEvent();
        final String guildId = event.getGuild().getId();

        messagesMap.compute(guildId, (k, v) -> {
            if(v != null)
                v.close();

            return new SettingsMessage(guildId, event);
        });
    }

    public static void handleMessageDelete(String guildId, String messageId) {
        if(messagesMap.containsKey(guildId) && messagesMap.get(guildId).getMessageId().equals(messageId))
            messagesMap.remove(guildId);
    }

    @Nonnull
    @Override
    public SlashCommandData createSlashCommand() {
        return Commands.slash(name, "Open the configuration menu");
    }
}
