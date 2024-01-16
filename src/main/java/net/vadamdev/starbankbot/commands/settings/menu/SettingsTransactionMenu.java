package net.vadamdev.starbankbot.commands.settings.menu;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.vadamdev.jdautils.smart.entities.SmartButton;
import net.vadamdev.jdautils.smart.entities.SmartModal;
import net.vadamdev.jdautils.smart.entities.SmartStringSelectMenu;
import net.vadamdev.jdautils.smart.messages.MessageContent;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.commands.settings.SettingsCommand;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.language.Lang;
import net.vadamdev.starbankbot.transaction.DistributionMode;
import net.vadamdev.starbankbot.utils.StarbankEmbed;
import net.vadamdev.starbankbot.utils.Utils;

/**
 * @author VadamDev
 * @since 12/01/2024
 */
public class SettingsTransactionMenu extends AbstractSettingsMenu {
    private final SmartModal PERCENTAGE_MODAL = SmartModal.of(
            Modal.create("StarBank-TransactionPercentageModal", "Starbank - Settings").addActionRow(
                    TextInput.create("percentage", "Percentage (from 1 to 100)", TextInputStyle.SHORT)
                            .setPlaceholder("10")
                            .setRequiredRange(1, 3)
                            .build()
            ).build(), event -> {
                if(!canUse(event.getMember()))
                    return;

                final GuildConfiguration config = Main.starbankBot.getGuildConfigManager().getOrDefault(event.getGuild());
                final Lang lang = config.getLang();

                try {
                    final int percentage = Integer.parseInt(event.getValue("percentage").getAsString());
                    if (percentage < 1 || percentage > 99) {
                        event.replyEmbeds(new StarbankEmbed()
                                .setTitle("Star Bank - " + lang.localize("settings.name"))
                                .setDescription(lang.localize("settings.transactionMenu.percentageError"))
                                .setColor(StarbankEmbed.ERROR_COLOR)
                                .build()).setEphemeral(true).queue();

                        return;
                    }

                    setConfigValue(config, "TRANSACTION_PERCENTAGE", percentage);

                    replySuccessMessage(event, lang);
                    SettingsMainMenu.SETTINGS_TRANSACTION_MENU.open(event.getMessage());
                } catch (Exception ignored) {
                    event.replyEmbeds(new StarbankEmbed()
                            .setTitle("Star Bank - " + lang.localize("settings.name"))
                            .setDescription(lang.localize("settings.transactionMenu.percentageError"))
                            .setColor(StarbankEmbed.ERROR_COLOR)
                            .build()).setEphemeral(true).queue();
                }
            });

    @Override
    public void init(Guild guild, MessageContent contents) {
        final GuildConfiguration config = Main.starbankBot.getGuildConfigManager().getOrDefault(guild);
        final Lang lang = config.getLang();
        final DistributionMode distributionMode = config.getDistributionMode();

        /*
           Embeds
         */

        contents.setEmbed(new StarbankEmbed()
                .setTitle("Star Bank - " + lang.localize("settings.name"))
                .setDescription(
                        lang.localize("settings.transactionMenu.description", str -> str
                                .replace("%distribution_mode%", distributionMode.getDisplayName())
                                .replace("%percentage_line%", distributionMode.equals(DistributionMode.PERCENTAGE) ? lang.localize("settings.transactionMenu.percentageLine", str1 -> str1.replace("%percentage%", String.valueOf(config.TRANSACTION_PERCENTAGE))) : "")
                                .replace("%allow_override%", Utils.displayBoolean(config.TRANSACTION_ALLOW_OVERRIDE)))
                )
                .setColor(StarbankEmbed.CONFIG_COLOR)
                .build()
        );

        /*
           Components
         */

        contents.addComponents(
                SmartStringSelectMenu.of(DistributionMode.createSelectionMenu(lang).setDefaultOptions(distributionMode.toSelectOption(lang)).build(), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    setConfigValue(config, "TRANSACTION_MODE", event.getValues().get(0));

                    replySuccessMessage(event, lang);
                    SettingsMainMenu.SETTINGS_TRANSACTION_MENU.open(event.getMessage());
                })
        );

        contents.addComponents(
                SmartButton.of(distributionMode.equals(DistributionMode.PERCENTAGE) ? Button.primary("StarBank-Settings-TransactionPercentage", "Percentage") : Button.primary("StarBank-Settings-TransactionPercentage", "Percentage").asDisabled(), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    PERCENTAGE_MODAL.open(event);
                }),

                SmartButton.of(Button.primary("StarBank-Settings-AllowOverride", "Allow Override Distribution Mode"), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    event.deferEdit().queue();
                    SettingsMainMenu.SETTINGS_OVERRIDE_MENU.open(event.getMessage());
                }),

                SmartButton.of(Button.secondary("StarBank-Settings-MainMenu", lang.localize("settings.back")), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    event.deferEdit().queue();
                    SettingsMainMenu.SETTINGS_MAIN_MENU.open(event.getMessage());
                })
        );
    }
}
