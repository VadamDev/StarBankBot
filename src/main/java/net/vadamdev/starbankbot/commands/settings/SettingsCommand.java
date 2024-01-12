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
import net.vadamdev.starbankbot.commands.settings.menu.SettingsMainMenu;

import javax.annotation.Nonnull;

/**
 * @author VadamDev
 * @since 30/12/2023
 */
public class SettingsCommand extends Command implements ISlashCommand {
    public static final Permission SETTINGS_PERMISSION = Permission.ADMINISTRATOR;

    public SettingsCommand() {
        super("settings");
        setPermission(SETTINGS_PERMISSION);
    }

    @Override
    public void execute(@Nonnull Member sender, @Nonnull ICommandData commandData) {
        final SlashCommandInteractionEvent event = commandData.castOrNull(SlashCmdData.class).getEvent();
        SettingsMainMenu.SETTINGS_MAIN_MENU.open(event);
    }

    @Nonnull
    @Override
    public SlashCommandData createSlashCommand() {
        return Commands.slash(name, "Open the configuration menu");
    }
}
