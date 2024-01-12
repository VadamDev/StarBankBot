package net.vadamdev.starbankbot.listeners;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.StarbankBot;

import javax.annotation.Nonnull;

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
        starbankBot.getTransactionManager().handleButtonInteractionEvent(event);
    }

    @Override
    public void onEntitySelectInteraction(@Nonnull EntitySelectInteractionEvent event) {
        starbankBot.getTransactionManager().handleEntitySelectInteractionEvent(event);
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        starbankBot.getTransactionManager().handleMessageDelete(event.getMessageId());
    }

    @Override
    public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
        event.getMessageIds().forEach(messageId -> starbankBot.getTransactionManager().handleMessageDelete(messageId));
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        starbankBot.getGuildConfigManager().deleteIfPresent(event.getGuild());
    }
}
