package net.vadamdev.starbankbot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.vadamdev.jdautils.commands.Command;
import net.vadamdev.jdautils.commands.ISlashCommand;
import net.vadamdev.jdautils.commands.data.ICommandData;
import net.vadamdev.jdautils.commands.data.SlashCmdData;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;

import javax.annotation.Nonnull;

/**
 * @author VadamDev
 * @since 01/01/2024
 */
public class AUECCommand extends Command implements ISlashCommand {
    public AUECCommand() {
        super("auec");
    }

    @Override
    public void execute(@Nonnull Member sender, @Nonnull ICommandData commandData) {
        final SlashCommandInteractionEvent event = commandData.castOrNull(SlashCmdData.class).getEvent();
        final GuildConfiguration config = Main.starbankBot.getGuildConfigManager().getOrDefault(event.getGuild());

        Main.starbankBot.getTransactionManager().beginTransaction(
                sender,
                event.getOption("amount", -1, OptionMapping::getAsInt),
                event.getOption("override", false, OptionMapping::getAsBoolean) && config.TRANSACTION_ALLOW_OVERRIDE,
                config,
                event
        );
    }

    @Nonnull
    @Override
    public SlashCommandData createSlashCommand() {
        return Commands.slash(name, "Start a transaction")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "amount", "Amount of aUEC")
                                .setMinValue(1)
                                .setRequired(true),
                        new OptionData(OptionType.BOOLEAN, "override", "If possible, set the transaction mode to DEFAULT")
                );
    }
}
