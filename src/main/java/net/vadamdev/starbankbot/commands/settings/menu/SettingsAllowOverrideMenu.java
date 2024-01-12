package net.vadamdev.starbankbot.commands.settings.menu;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vadamdev.jdautils.smart.entities.SmartButton;
import net.vadamdev.jdautils.smart.messages.MessageContent;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.utils.StarbankEmbed;

import java.util.function.Consumer;

/**
 * @author VadamDev
 * @since 12/01/2024
 */
public class SettingsAllowOverrideMenu extends AbstractSettingsMenu {
    @Override
    public void init(Guild guild, MessageContent contents) {
        final GuildConfiguration config = Main.starbankBot.getGuildConfigManager().getOrDefault(guild);

        /*
           Embeds
         */

        contents.setEmbed(new StarbankEmbed()
                .setTitle("Star Bank - Settings")
                .setColor(StarbankEmbed.CONFIG_COLOR)
                .build()
        );

        /*
           Components
         */

        final boolean canOverride = config.TRANSACTION_ALLOW_OVERRIDE;

        final Button trueButton = Button.secondary("StarBank-Settings-AllowOverride-True", Emoji.fromUnicode("✅"));
        final Button falseButton = Button.secondary("StarBank-Settings-AllowOverride-False", Emoji.fromUnicode("❌"));

        final Consumer<ButtonInteractionEvent> consumer = event -> {
            setConfigValue(config, "TRANSACTION_ALLOW_OVERRIDE", !canOverride);

            replySuccessMessage(event);
            SettingsMainMenu.SETTINGS_TRANSACTION_MENU.open(event.getMessage());
        };

        contents.addComponents(
                SmartButton.of(!canOverride ? trueButton : trueButton.asDisabled(), consumer),
                SmartButton.of(canOverride ? falseButton : falseButton.asDisabled(), consumer)
        );
    }
}
