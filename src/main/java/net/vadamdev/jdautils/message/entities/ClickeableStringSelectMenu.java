package net.vadamdev.jdautils.message.entities;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public class ClickeableStringSelectMenu implements IClickeableComponent<StringSelectInteractionEvent> {
    public static ClickeableStringSelectMenu of(StringSelectMenu selectMenu, Consumer<StringSelectInteractionEvent> consumer) {
        return new ClickeableStringSelectMenu(selectMenu, consumer);
    }

    private final StringSelectMenu selectMenu;
    private final Consumer<StringSelectInteractionEvent> consumer;

    private ClickeableStringSelectMenu(StringSelectMenu selectMenu, Consumer<StringSelectInteractionEvent> consumer) {
        this.selectMenu = selectMenu;
        this.consumer = consumer;
    }

    @Override
    public void run(StringSelectInteractionEvent event) {
        consumer.accept(event);
    }

    @Override
    public ActionComponent getComponent() {
        return selectMenu;
    }
}
