package net.vadamdev.jdautils.message;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.vadamdev.jdautils.message.entities.IClickeableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public class MessageContent {
    private List<MessageEmbed> embeds;
    private final List<IClickeableComponent> interactables;

    public MessageContent() {
        this.embeds = new ArrayList<>();
        this.interactables = new ArrayList<>();
    }

    public void setEmbed(MessageEmbed... embeds) {
        this.embeds = Arrays.asList(embeds);
    }

    public void setEmbed(List<MessageEmbed> embeds) {
        this.embeds = embeds;
    }

    public void add(IClickeableComponent<? extends GenericInteractionCreateEvent> component) {
        interactables.add(component);
    }

    public List<MessageEmbed> getEmbeds() {
        return embeds;
    }


    public List<IClickeableComponent> getClickeableComponents() {
        return interactables;
    }

    public List<ItemComponent> getInteractables() {
        return interactables.stream().map(IClickeableComponent::getComponent).collect(Collectors.toList());
    }
}
