package net.vadamdev.jdautils;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.jdautils.commands.CommandHandler;

import javax.annotation.Nonnull;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public class JDAUListener extends ListenerAdapter {
    private final CommandHandler commandHandler;

    public JDAUListener(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        commandHandler.handleMessageReceive(event);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        commandHandler.handleSlashCommandInteraction(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
        commandHandler.handleCommandAutoCompleteInteraction(event);
    }

    /*@Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        SmartMessageManager.handleButtonInteraction(event);
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        SmartMessageManager.handleStringSelectInteraction(event);
    }

    @Override
    public void onEntitySelectInteraction(@Nonnull EntitySelectInteractionEvent event) {
        SmartMessageManager.handleEntitySelectInteraction(event);
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        SmartMessageManager.handleMessageDelete(event.getGuild().getId(), event.getMessageId());
    }

    @Override
    public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
        event.getMessageIds().forEach(messageId -> SmartMessageManager.handleMessageDelete(event.getGuild().getId(), messageId));
    }*/
}
