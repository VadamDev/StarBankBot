package net.vadamdev.jdautils.message.entities;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public class ClickeableButton implements IClickeableComponent<ButtonInteractionEvent> {
    public static ClickeableButton empty(Button button) {
        if(!button.getStyle().equals(ButtonStyle.LINK))
            return of(button, event -> event.deferEdit().queue());

        return of(button, event -> {});
    }

    public static ClickeableButton of(Button button, Consumer<ButtonInteractionEvent> consumer) {
        return new ClickeableButton(button, consumer);
    }

    private final Button button;
    private final Consumer<ButtonInteractionEvent> consumer;

    private ClickeableButton(Button button, Consumer<ButtonInteractionEvent> consumer) {
        this.button = button;
        this.consumer = consumer;
    }

    @Override
    public void run(ButtonInteractionEvent event) {
        consumer.accept(event);
    }

    @Override
    public ActionComponent getComponent() {
        return button;
    }
}
