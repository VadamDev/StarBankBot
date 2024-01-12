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
                if(!event.getMember().hasPermission(SettingsCommand.SETTINGS_PERMISSION))
                    return;

                try {
                    final int percentage = Integer.parseInt(event.getValue("percentage").getAsString());

                    if (percentage < 1 || percentage > 99) {
                        event.replyEmbeds(new StarbankEmbed()
                                .setTitle("Star Bank - Settings")
                                .setDescription("You need to specify a number between 1 and 99 !")
                                .setColor(StarbankEmbed.ERROR_COLOR)
                                .build()).setEphemeral(true).queue();

                        return;
                    }

                    setConfigValue(Main.starbankBot.getGuildConfigManager().getOrDefault(event.getGuild()), "TRANSACTION_PERCENTAGE", percentage);

                    replySuccessMessage(event);
                    SettingsMainMenu.SETTINGS_TRANSACTION_MENU.open(event.getMessage());
                } catch (Exception ignored) {
                    event.replyEmbeds(new StarbankEmbed()
                            .setTitle("Star Bank - Settings")
                            .setDescription("You need to specify a number between 1 and 100 !")
                            .setColor(StarbankEmbed.ERROR_COLOR)
                            .build()).setEphemeral(true).queue();
                }
            });

    @Override
    public void init(Guild guild, MessageContent contents) {
        final GuildConfiguration config = Main.starbankBot.getGuildConfigManager().getOrDefault(guild);
        final DistributionMode distributionMode = config.getDistributionMode();

        /*
           Embeds
         */

        contents.setEmbed(new StarbankEmbed()
                .setTitle("Star Bank - Settings")
                .setDescription(
                        "> Transaction Mode: " + distributionMode.name() + "\n" +
                        (distributionMode == DistributionMode.PERCENTAGE ? "> Percentage: " + config.TRANSACTION_PERCENTAGE + "%\n" : "") +
                        "> Allow Override Transaction Mode: " + Utils.displayBoolean(config.TRANSACTION_ALLOW_OVERRIDE)
                )
                .setColor(StarbankEmbed.CONFIG_COLOR)
                .build()
        );

        /*
           Components
         */

        final StringSelectMenu.Builder transactionSelectMenu = DistributionMode.createSelectionMenu();
        transactionSelectMenu.setDefaultOptions(distributionMode.toSelectOption());

        contents.addComponents(
                SmartStringSelectMenu.of(transactionSelectMenu.build(), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    setConfigValue(config, "TRANSACTION_MODE", event.getValues().get(0));

                    replySuccessMessage(event);

                    SettingsMainMenu.SETTINGS_TRANSACTION_MENU.open(event.getMessage());
                })
        );

        contents.addComponents(
                SmartButton.of(config.getDistributionMode().equals(DistributionMode.PERCENTAGE) ? Button.primary("StarBank-Settings-TransactionPercentage", "Percentage") : Button.primary("StarBank-Settings-TransactionPercentage", "Percentage").asDisabled(), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    PERCENTAGE_MODAL.open(event);
                }),

                SmartButton.of(Button.primary("StarBank-Settings-AllowOverride", "Allow Override Transaction Mode"), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    event.deferEdit().queue();

                    SettingsMainMenu.SETTINGS_OVERRIDE_MENU.open(event.getMessage());
                }),

                SmartButton.of(Button.secondary("StarBank-Settings-MainMenu", "Go Back"), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    event.deferEdit().queue();
                    SettingsMainMenu.SETTINGS_MAIN_MENU.open(event.getMessage());
                })
        );
    }
}
