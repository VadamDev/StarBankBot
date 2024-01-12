package net.vadamdev.starbankbot.transaction;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.utils.StarbankEmbed;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author VadamDev
 * @since 05/01/2024
 */
public class TransactionManager {
    private final Map<String, List<Transaction>> transactionMap;

    public TransactionManager() {
        this.transactionMap = new HashMap<>();
    }

    public void beginTransaction(Member owner, int amount, boolean ignoreConfig, GuildConfiguration config, IReplyCallback callback) {
        if(amount == -1) {
            Main.logger.error("Invalid transaction amount ! (Created by " + owner.getId() + ")");
            return;
        }

        transactionMap.computeIfAbsent(owner.getId(), k -> new ArrayList<>()).add(new Transaction(owner, amount, ignoreConfig, config, callback));
    }

    public void removeTransaction(Transaction transaction) {
        transactionMap.computeIfPresent(transaction.getOwnerId(), (key, transactions) -> {
            transactions.remove(transaction);
            return !transactions.isEmpty() ? transactions : null;
        });
    }

    /*
       Events
     */

    public void handleButtonInteractionEvent(@Nonnull ButtonInteractionEvent event) {
        final String componentId = event.getComponentId();

        if(componentId.startsWith("StarBank-Transaction-Add-Channel_")) {
            findTransactionByMessageId(componentId.split("_")[1])
                    .ifPresent(transaction -> {
                        event.replyEmbeds(new StarbankEmbed()
                                    .setTitle("Star Bank - " + transaction.getAmount() + " aUEC")
                                    .setColor(StarbankEmbed.CONFIG_COLOR).build())
                            .setEphemeral(true).setActionRow(
                                    EntitySelectMenu.create("StarBank-Transaction-ChannelSelectMenu_" + transaction.getMessageId(), EntitySelectMenu.SelectTarget.CHANNEL)
                                            .setChannelTypes(ChannelType.VOICE)
                                            .setRequiredRange(1, 3)
                                            .build()
                            ).queue();
                    });

            return;
        }else if(componentId.startsWith("StarBank-Transaction-Add-Member_")) {
            findTransactionByMessageId(componentId.split("_")[1])
                    .ifPresent(transaction -> {
                        event.replyEmbeds(new StarbankEmbed()
                                        .setTitle("Star Bank - " + transaction.getAmount() + " aUEC")
                                        .setColor(StarbankEmbed.CONFIG_COLOR).build())
                                .setEphemeral(true).setActionRow(
                                        EntitySelectMenu.create("StarBank-Transaction-UserSelectMenu_" + transaction.getMessageId(), EntitySelectMenu.SelectTarget.USER)
                                                .setRequiredRange(1, 5)
                                                .build()
                                ).queue();
                    });

            return;
        }

        final String messageId = event.getMessageId();

        switch(componentId) {
            case "StarBank-Transaction-Use":
                findTransactionByMessageId(messageId)
                        .ifPresent(transaction -> transaction.computeUseButton(event.getUser().getId(), event));

                break;
            case "StarBank-Transaction-Close":
                findTransactionByMessageId(messageId)
                        .ifPresent(transaction -> transaction.computeCloseButton(event.getMember(), event));

                break;
            case "StarBank-Transaction-Add":
                findTransactionByMessageId(messageId)
                        .ifPresent(transaction -> {
                            if(!event.getMember().getId().equals(transaction.getOwnerId())) {
                                event.replyEmbeds(new StarbankEmbed()
                                        .setDescription(transaction.getConfig().getLang().localize("transaction.error.not_owner"))
                                        .setColor(StarbankEmbed.ERROR_COLOR).build()).setEphemeral(true).queue();

                                return;
                            }

                            event.replyEmbeds(new StarbankEmbed()
                                    .setTitle("Star Bank - " + transaction.getAmount() + " aUEC")
                                    .setDescription(transaction.getConfig().getLang().localize("transaction.add.message"))
                                    .setColor(StarbankEmbed.CONFIG_COLOR).build()
                            ).setEphemeral(true).setActionRow(
                                    Button.secondary("StarBank-Transaction-Add-Channel_" + transaction.getMessageId(), Emoji.fromUnicode("#️⃣")),
                                    Button.secondary("StarBank-Transaction-Add-Member_" + transaction.getMessageId(), Emoji.fromUnicode("\uD83D\uDC64"))
                            ).queue();
                        });

                break;
            default:
                break;
        }
    }

    public void handleEntitySelectInteractionEvent(@Nonnull EntitySelectInteractionEvent event) {
        final String componentId = event.getComponentId();

        if(componentId.startsWith("StarBank-Transaction-ChannelSelectMenu_")) {
            findTransactionByMessageId(componentId.split("_")[1]).ifPresent(transaction -> {
                event.deferEdit().queue();

                transaction.addUsers(
                        event.getMentions().getChannels(VoiceChannel.class).stream()
                                .flatMap(channel -> channel.getMembers().stream())
                                .map(ISnowflake::getId)
                                .collect(Collectors.toList())
                );
            });
        }else if(componentId.startsWith("StarBank-Transaction-UserSelectMenu_")) {
            findTransactionByMessageId(componentId.split("_")[1]).ifPresent(transaction -> {
                event.deferEdit().queue();
                transaction.addUsers(event.getMentions().getUsers().stream().map(ISnowflake::getId).collect(Collectors.toList()));
            });
        }
    }

    public void handleMessageDelete(String messageId) {
        findTransactionByMessageId(messageId).ifPresent(this::removeTransaction);
    }

    /*
       Utils
     */

    private Optional<Transaction> findTransactionByMessageId(String messageId) {
        return transactionMap.values().stream()
                .flatMap(List::stream)
                .filter(transaction -> transaction.getMessageId().equals(messageId))
                .findFirst();
    }
}
