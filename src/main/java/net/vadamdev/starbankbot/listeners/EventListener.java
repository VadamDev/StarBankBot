package net.vadamdev.starbankbot.listeners;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.StarbankBot;
import net.vadamdev.starbankbot.commands.settings.SettingsCommand;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author VadamDev
 * @since 03/01/2024
 */
public class EventListener extends ListenerAdapter {
    private final StarbankBot starbankBot;

    public EventListener() {
        this.starbankBot = Main.starbankBot;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        Optional.ofNullable(SettingsCommand.messagesMap.get(event.getGuild().getId()))
                .ifPresent(message -> message.handleButtonInteractionEvent(event));

        starbankBot.getTransactionManager().handleButtonInteractionEvent(event);
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        Optional.ofNullable(SettingsCommand.messagesMap.get(event.getGuild().getId()))
                .ifPresent(message -> message.handleSelectInteractionEvent(event));
    }

    @Override
    public void onEntitySelectInteraction(@Nonnull EntitySelectInteractionEvent event) {
        starbankBot.getTransactionManager().handleEntitySelectInteractionEvent(event);
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        Optional.ofNullable(SettingsCommand.messagesMap.get(event.getGuild().getId()))
                .ifPresent(message -> message.handleModalInteractionEvent(event));
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        final String messageId = event.getMessageId();

        starbankBot.getTransactionManager().handleMessageDelete(messageId);
        SettingsCommand.handleMessageDelete(event.getGuild().getId(), messageId);
    }

    @Override
    public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
        final String guildId = event.getGuild().getId();

        event.getMessageIds().forEach(messageId -> {
            starbankBot.getTransactionManager().handleMessageDelete(messageId);
            SettingsCommand.handleMessageDelete(guildId, messageId);
        });
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        starbankBot.getGuildConfigManager().deleteIfPresent(event.getGuild());
    }
}
