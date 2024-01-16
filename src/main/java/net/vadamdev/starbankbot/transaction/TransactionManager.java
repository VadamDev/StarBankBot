package net.vadamdev.starbankbot.transaction;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;

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
        final String messageId = event.getMessageId();
        final String userId = event.getUser().getId();

        switch(event.getComponentId()) {
            case "StarBank-Transaction-Use":
                findTransactionByMessageId(messageId)
                        .ifPresent(transaction -> transaction.computeUseButton(userId, event));

                break;
            case "StarBank-Transaction-Close":
                findTransactionByMessageId(messageId)
                        .ifPresent(transaction -> transaction.computeCloseButton(userId, event));

                break;
            case "StarBank-Transaction-Add":
                findTransactionByMessageId(messageId)
                        .ifPresent(transaction -> transaction.computeAddButton(userId, event));

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
                                .map(Member::getUser)
                                .collect(Collectors.toList())
                );
            });
        }else if(componentId.startsWith("StarBank-Transaction-UserSelectMenu_")) {
            findTransactionByMessageId(componentId.split("_")[1]).ifPresent(transaction -> {
                event.deferEdit().queue();
                transaction.addUsers(event.getMentions().getUsers());
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
