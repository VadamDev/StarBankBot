package net.vadamdev.starbankbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.vadamdev.starbankbot.Main;

import java.awt.*;

/**
 * @author VadamDev
 * @since 10/01/2024
 */
public class StarbankEmbed extends EmbedBuilder {
    public static final Color NEUTRAL_COLOR = Color.ORANGE;
    public static final Color SUCCESS_COLOR = Color.GREEN;
    public static final Color ERROR_COLOR = Color.RED;
    public static final Color CONFIG_COLOR = new Color(52, 152, 219);

    public StarbankEmbed() {
        setFooter("Starbank - By VadamDev", Main.starbankBot.getAvatarURL());
    }
}
