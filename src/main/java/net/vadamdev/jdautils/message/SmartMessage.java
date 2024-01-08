package net.vadamdev.jdautils.message;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.vadamdev.jdautils.message.entities.IClickeableComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public class SmartMessage {
    private final SmartMessageProvider provider;
    private final MessageContent content;

    private SmartMessage(SmartMessageProvider provider, MessageContent content) {
        this.provider = provider;
        this.content = content;
    }

    public void send(IReplyCallback callback) {
        final MessageContent content = computeContent(callback.getGuild());

        callback.replyEmbeds(content.getEmbeds())
                .setActionRow(content.getInteractables())
                .queue(hook -> {
                    if(content.getInteractables().isEmpty())
                        return;

                    hook.retrieveOriginal().queue(msg -> SmartMessageManager.register(msg, content.getClickeableComponents()));
                });
    }

    public void send(TextChannel channel) {
        final MessageContent content = computeContent(channel.getGuild());

        channel.sendMessageEmbeds(content.getEmbeds())
                .setActionRow(content.getInteractables())
                .queue(msg -> {
                    if(content.getInteractables().isEmpty())
                        return;

                    SmartMessageManager.register(msg, content.getClickeableComponents());
                });
    }

    private MessageContent computeContent(Guild guild) {
        if(provider != null) {
            final MessageContent content = new MessageContent();
            provider.init(guild, content);
            provider.populate(guild, content);

            return content;
        }else
            return content;
    }

    public static SmartMessage fromProvider(SmartMessageProvider provider) {
        return new SmartMessage(provider, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<MessageEmbed> embeds;
        private final List<IClickeableComponent<? extends GenericInteractionCreateEvent>> interactables;

        private Builder() {
            this.interactables = new ArrayList<>();
        }

        public Builder setEmbed(MessageEmbed... embeds) {
            this.embeds = Arrays.asList(embeds);
            return this;
        }

        public Builder addComponent(IClickeableComponent<? extends GenericInteractionCreateEvent> component) {
            interactables.add(component);
            return this;
        }

        public Builder addComponents(IClickeableComponent<? extends GenericInteractionCreateEvent>... components) {
            interactables.addAll(Arrays.asList(components));
            return this;
        }

        public SmartMessage build() {
            if(embeds.isEmpty())
                throw new IllegalStateException("You need to an a minimum of one embed");

            MessageContent content = new MessageContent();
            content.setEmbed(embeds);
            interactables.forEach(content::add);

            return new SmartMessage(null, content);
        }
    }
}
