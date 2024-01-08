package net.vadamdev.jdautils.message.entities;

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;

import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public class ClickeableEntitySelectMenu implements IClickeableComponent<EntitySelectInteractionEvent> {
    public static ClickeableEntitySelectMenu of(EntitySelectMenu selectMenu, Consumer<EntitySelectInteractionEvent> consumer) {
        return new ClickeableEntitySelectMenu(selectMenu, consumer);
    }

    private final EntitySelectMenu selectMenu;
    private final Consumer<EntitySelectInteractionEvent> consumer;

    private ClickeableEntitySelectMenu(EntitySelectMenu selectMenu, Consumer<EntitySelectInteractionEvent> consumer) {
        this.selectMenu = selectMenu;
        this.consumer = consumer;
    }

    @Override
    public void run(EntitySelectInteractionEvent event) {
        consumer.accept(event);
    }

    @Override
    public ActionComponent getComponent() {
        return selectMenu;
    }
}
