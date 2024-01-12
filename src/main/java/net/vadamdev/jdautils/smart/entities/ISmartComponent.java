package net.vadamdev.jdautils.smart.entities;

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ActionComponent;

/**
 * @author VadamDev
 * @since 08/01/2024
 */
public interface ISmartComponent<T extends GenericInteractionCreateEvent> {
    void run(T t);

    ActionComponent getComponent();
}
