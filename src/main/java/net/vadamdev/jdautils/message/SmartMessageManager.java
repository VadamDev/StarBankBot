package net.vadamdev.jdautils.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.vadamdev.jdautils.message.entities.IClickeableComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public final class SmartMessageManager {
    private static final Map<String, Map<String, List<IClickeableComponent>>> smartMessages = new HashMap<>();

    public static void register(Message message, List<IClickeableComponent> components) {
        smartMessages.computeIfAbsent(message.getGuildId(), k -> new HashMap<>())
                .put(message.getId(), components);
    }

    public static void unregister(String guildId, String messageId) {
        smartMessages.computeIfPresent(guildId, (k, v) -> {
            v.remove(messageId);

            if(v.isEmpty())
                return null;

            return v;
        });
    }

    public static void unregister(Message message) {
        unregister(message.getGuildId(), message.getId());
    }

    public static void handleButtonInteraction(ButtonInteractionEvent event) {
        findComponent(event.getGuild().getId(), event.getMessageId(), event.getComponentId())
                .ifPresent(component -> component.run(event));
    }

    public static void handleStringSelectInteraction(StringSelectInteractionEvent event) {
        findComponent(event.getGuild().getId(), event.getMessageId(), event.getComponentId())
                .ifPresent(component -> component.run(event));
    }

    public static void handleEntitySelectInteraction(EntitySelectInteractionEvent event) {
        findComponent(event.getGuild().getId(), event.getMessageId(), event.getComponentId())
                .ifPresent(component -> component.run(event));
    }

    public static void handleMessageDelete(String guildId, String messageId) {
        unregister(guildId, messageId);
    }

    private static Optional<IClickeableComponent> findComponent(String guildId, String messageId, String componentId) {
        if(!smartMessages.containsKey(guildId))
            return Optional.empty();

        final Map<String, List<IClickeableComponent>> subMap = smartMessages.get(guildId);
        if(!subMap.containsKey(messageId))
            return Optional.empty();

        return subMap.get(messageId).stream()
                .filter(component -> component.getComponent().getId().equals(componentId))
                .findFirst();
    }
}
