package net.vadamdev.starbankbot.commands.settings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.language.Lang;
import net.vadamdev.starbankbot.transaction.DistributionMode;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * TODO: create a menu library to avoid this mess
 *
 * @author VadamDev
 * @since 03/01/2024
 */
public class SettingsMessage {
    private static final Color CONFIG_COLOR = new Color(52, 152, 219);

    private final String guildId;
    private final GuildConfiguration config;

    private Message message;

    public SettingsMessage(String guildId, IReplyCallback callback) {
        this.guildId = guildId;
        this.config = Main.starbankBot.getGuildConfigManager().getOrDefault(guildId);

        callback.replyEmbeds(createMainMenu())
                .setActionRow(
                        Button.primary("StarBank-Settings-Language", config.getLang().getFlag()),
                        Button.primary("StarBank-Settings-Transaction", Emoji.fromUnicode("\uD83D\uDCB5")),
                        Button.danger("StarBank-Settings-Close", "Close")
                ).queue(a -> a.retrieveOriginal().queue(message -> this.message = message));
    }

    public void close() {
        message.getChannel().retrieveMessageById(message.getId())
                .queue(message -> message.editMessageComponents(message.getComponents().stream().map(LayoutComponent::asDisabled).collect(Collectors.toList())).queue());
    }

    /*
       Listeners
     */

    public void handleButtonInteractionEvent(@Nonnull ButtonInteractionEvent event) {
        if(!event.getMember().hasPermission(SettingsCommand.SETTINGS_PERMISSION))
            return;

        switch(event.getComponentId()) {
            case "StarBank-Settings-Language":
                event.deferEdit().queue();

                final StringSelectMenu.Builder languageSelectMenu = Lang.createSelectionMenu();
                languageSelectMenu.setDefaultOptions(config.getLang().toSelectOption());

                message.editMessageEmbeds(new EmbedBuilder()
                                .setTitle("Star Bank - Settings")
                                .setDescription("Please select a language in the list below")
                                .setColor(CONFIG_COLOR)
                                .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL())
                                .build()
                ).setActionRow(languageSelectMenu.build()).queue();

                break;
            case "StarBank-Settings-Transaction":
                event.deferEdit().queue();

                final Button percentageButton = Button.primary("StarBank-Settings-TransactionPercentage", "Percentage");

                message.editMessageEmbeds(createTransactionMenu())
                        .setActionRow(
                                Button.primary("StarBank-Settings-TransactionMode", "Transaction Mode"),
                                config.getDistributionMode().equals(DistributionMode.PERCENTAGE) ? percentageButton : percentageButton.asDisabled(),
                                Button.primary("StarBank-Settings-AllowOverride", "Allow Override Transaction Mode"),
                                Button.secondary("StarBank-Settings-MainMenu", "Go Back")
                        ).queue();

                break;
            case "StarBank-Settings-Close":
                event.deferEdit().queue();

                SettingsCommand.messagesMap.remove(guildId);
                close();

                break;
            case "StarBank-Settings-MainMenu":
                event.deferEdit().queue();

                goToMainMenu();

                break;
            case "StarBank-Settings-TransactionMode":
                event.deferEdit().queue();

                final StringSelectMenu.Builder transactionSelectMenu = DistributionMode.createSelectionMenu();
                transactionSelectMenu.setDefaultOptions(config.getDistributionMode().toSelectOption());

                message.editMessageEmbeds(new EmbedBuilder()
                                .setTitle("Star Bank - Settings")
                                .setDescription("Please select a mode in the list below")
                                .setColor(CONFIG_COLOR)
                                .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL())
                                .build())
                        .setActionRow(transactionSelectMenu.build()).queue();

                break;
            case "StarBank-Settings-TransactionPercentage":
                event.replyModal(Modal.create("StarBank-TransactionPercentageModal", "StarBank - Settings")
                        .addActionRow(
                                TextInput.create("percentage", "Percentage (from 1 to 100)", TextInputStyle.SHORT)
                                        .setPlaceholder("10")
                                        .setRequiredRange(1, 3)
                                        .build()
                        ).build()).queue();

                break;
            case "StarBank-Settings-AllowOverride":
                event.deferEdit().queue();

                final boolean canOverride = config.TRANSACTION_ALLOW_OVERRIDE;

                final Button trueButton = Button.secondary("StarBank-Settings-AllowOverride-True", Emoji.fromUnicode("✅"));
                final Button falseButton = Button.secondary("StarBank-Settings-AllowOverride-False", Emoji.fromUnicode("❌"));

                message.editMessageEmbeds(new EmbedBuilder()
                                .setTitle("Star Bank - Settings")
                                .setColor(CONFIG_COLOR)
                                .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL())
                                .build())
                        .setActionRow(
                                !canOverride ? trueButton : trueButton.asDisabled(),
                                canOverride ? falseButton : falseButton.asDisabled()
                        ).queue();

                break;
            case "StarBank-Settings-AllowOverride-True": case "StarBank-Settings-AllowOverride-False":
                setConfigValue("TRANSACTION_ALLOW_OVERRIDE", !config.TRANSACTION_ALLOW_OVERRIDE);

                replySuccess(event);
                goToMainMenu();

                break;
            default:
                break;
        }
    }

    public void handleSelectInteractionEvent(@Nonnull StringSelectInteractionEvent event) {
        if(!event.getMember().hasPermission(SettingsCommand.SETTINGS_PERMISSION))
            return;

        switch(event.getComponentId()) {
            case "StarBank-LanguageSelectMenu":
                setConfigValue("LANG", event.getValues().get(0));

                replySuccess(event);
                goToMainMenu();

                break;
            case "StarBank-TransactionSelectMenu":
                setConfigValue("TRANSACTION_MODE", event.getValues().get(0));

                replySuccess(event);
                goToMainMenu();

                break;
            default:
                break;
        }
    }

    public void handleModalInteractionEvent(@Nonnull ModalInteractionEvent event) {
        if(!event.getMember().hasPermission(SettingsCommand.SETTINGS_PERMISSION))
            return;

        if(event.getModalId().equals("StarBank-TransactionPercentageModal")) {
            try {
                final int percentage = Integer.parseInt(event.getValue("percentage").getAsString());

                if (percentage < 1 || percentage > 100) {
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("Star Bank - Settings")
                            .setDescription("You need to specify a number between 1 and 100 !")
                            .setColor(Color.RED)
                            .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL()).build()
                    ).setEphemeral(true).queue();

                    return;
                }

                setConfigValue("TRANSACTION_PERCENTAGE", percentage);

                replySuccess(event);
                goToMainMenu();
            } catch (Exception ignored) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("Star Bank - Settings")
                        .setDescription("You need to specify a number between 1 and 100 !")
                        .setColor(Color.RED)
                        .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL()).build()
                ).setEphemeral(true).queue();
            }
        }
    }

    /*
       Messages
     */

    private void goToMainMenu() {
        message.editMessageEmbeds(createMainMenu())
                .setActionRow(
                        Button.primary("StarBank-Settings-Language", config.getLang().getFlag()),
                        Button.primary("StarBank-Settings-Transaction", Emoji.fromUnicode("\uD83D\uDCB5")),
                        Button.danger("StarBank-Settings-Close", "Close")
                ).queue();
    }

    private MessageEmbed createMainMenu() {
        final Lang lang = config.getLang();
        final DistributionMode distributionMode = config.getDistributionMode();

        return new EmbedBuilder()
                .setTitle("Star Bank - Settings")
                .setDescription(
                        "> Language: " + lang.getDisplayName() + " (" + lang.getFlag().getFormatted() + ")\n" +
                        "\n" +
                        "> Transaction Mode: " + distributionMode.name() + "\n" +
                        (distributionMode == DistributionMode.PERCENTAGE ? "> Percentage: " + config.TRANSACTION_PERCENTAGE + "%\n" : "") +
                        "> Allow Override Transaction Mode: " + config.TRANSACTION_ALLOW_OVERRIDE + "\n"
                )
                .setColor(CONFIG_COLOR)
                .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL())
                .build();
    }

    private MessageEmbed createTransactionMenu() {
        final DistributionMode distributionMode = config.getDistributionMode();

        return new EmbedBuilder()
                .setTitle("Star Bank - Settings")
                .setDescription(
                        "> Transaction Mode: " + distributionMode.name() + "\n" +
                        (distributionMode == DistributionMode.PERCENTAGE ? "> Percentage: " + config.TRANSACTION_PERCENTAGE + "%\n" : "") +
                        "> Allow Override Transaction Mode: " + config.TRANSACTION_ALLOW_OVERRIDE
                )
                .setColor(CONFIG_COLOR)
                .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL())
                .build();
    }

    private MessageEmbed createSuccessEmbed() {
        return new EmbedBuilder()
                .setTitle("Star Bank - Settings")
                .setDescription("Your changes have been saved !")
                .setColor(Color.GREEN)
                .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL()).build();
    }

    /*
       Utils
     */

    private void replySuccess(IReplyCallback callback) {
        callback.replyEmbeds(createSuccessEmbed()).setEphemeral(true).queue();
    }

    private void setConfigValue(String name, Object value) {
        try {
            config.setValue(name, value);
            config.save();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected String getMessageId() {
        return message.getId();
    }
}
