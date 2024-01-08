package net.vadamdev.starbankbot.transaction;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.language.Lang;
import net.vadamdev.starbankbot.utils.Utils;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author VadamDev
 * @since 04/01/2024
 */
public class Transaction {
    private final String ownerId;
    private final int amount;
    private final boolean ignoreConfig;
    private final GuildConfiguration config;

    private final Map<String, Boolean> users;

    private Message message;

    public Transaction(Member owner, int amount, boolean ignoreConfig, GuildConfiguration config, IReplyCallback callback) {
        this.ownerId = owner.getId();
        this.amount = amount;
        this.ignoreConfig = ignoreConfig;
        this.config = config;

        this.users = new HashMap<>();

        final GuildVoiceState voiceState = owner.getVoiceState();
        if(voiceState.inAudioChannel())
            voiceState.getChannel().getMembers().forEach(member -> users.putIfAbsent(member.getId(), false));
        else
            users.put(ownerId, false);

        callback.replyEmbeds(createTransactionEmbed())
                .setActionRow(
                        Button.success("StarBank-Transaction-Use", Emoji.fromUnicode("✅")),
                        Button.danger("StarBank-Transaction-Close", Emoji.fromUnicode("\uD83D\uDD12")),
                        Button.secondary("StarBank-Transaction-Add", Emoji.fromUnicode("➕"))
                ).queue(a -> a.retrieveOriginal().queue(message -> this.message = message));
    }

    public void addUsers(Collection<String> userIds) {
        userIds.forEach(userId -> users.putIfAbsent(userId, false));
        refreshMessage();
    }

    private void refreshMessage() {
        message.editMessageEmbeds(createTransactionEmbed()).queue();
    }

    /*
       Buttons
     */

    protected void computeUseButton(String userId, GenericComponentInteractionCreateEvent callback) {
        if(users.containsKey(userId)) {
            callback.deferEdit().queue();

            users.replace(userId, !users.get(userId));
            refreshMessage();
        }else
            callback.replyEmbeds(new EmbedBuilder()
                    .setDescription(config.getLang().localize("transaction.error.not_user"))
                    .setColor(Color.RED)
                    .setFooter("StarBank - By VadamDev").build()).setEphemeral(true).queue();
    }

    protected void computeCloseButton(Member member, IReplyCallback event) {
        if(!member.getId().equals(ownerId)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(config.getLang().localize("transaction.error.not_owner"))
                    .setColor(Color.RED)
                    .setFooter("StarBank - By VadamDev").build()).setEphemeral(true).queue();

            return;
        }

        Main.starbankBot.getTransactionManager().removeTransaction(this);
        message.editMessageComponents(message.getActionRows().stream().map(ActionRow::asDisabled).collect(Collectors.toList())).queue();

        event.replyEmbeds(createEndTransactionEmbed()).queue();
    }

    /*
       Embeds Creation
     */

    private MessageEmbed createTransactionEmbed() {
        final Lang lang = config.getLang();

        final StringBuilder description = new StringBuilder(
                "> " + lang.localize("distributionMode.description." + (ignoreConfig ? DistributionMode.DEFAULT : config.getDistributionMode()).name())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%percentage%", String.valueOf(config.TRANSACTION_PERCENTAGE)) + "\n" +
                "\n" +
                lang.localize("transaction.header") + "\n"
        );

        users.forEach((userId, b) -> description.append("> (" + Utils.formatBoolean(b) + ") - <@" + userId + ">\n"));

        description.append("\n" + lang.localize("transaction.footer"));

        return new EmbedBuilder()
                .setTitle("Star Bank - " + amount + " aUEC")
                .setDescription(description.toString())
                .setColor(Color.ORANGE)
                .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL())
                .build();
    }

    private MessageEmbed createEndTransactionEmbed() {
        final Lang lang = config.getLang();
        final List<String> members = users.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        final StringBuilder description = new StringBuilder(lang.localize("transaction.result.header")
                .replace("%member_count%", members.size()+"")+ "\n");

        members.forEach(userId -> description.append("> <@" + userId + ">\n"));

        final double[] result = (ignoreConfig ? DistributionMode.DEFAULT : config.getDistributionMode()).distribute(amount, members.size(), config);
        final NumberFormat numberFormat = NumberFormat.getInstance(lang.toLocale());

        final double member = Math.floor(result[0] - (0.005 * result[0]));
        final double bot = Math.floor(result[1] - (0.005 * result[1]));

        description.append("\n" + lang.localize("transaction.result.footer")
                .replace("%member%", numberFormat.format(member))
                .replace("%bot%", numberFormat.format(bot)));

        return new EmbedBuilder()
                .setTitle("Star Bank - " + amount + " aUEC")
                .setDescription(description.toString())
                .setColor(Color.GREEN)
                .setFooter("StarBank - By VadamDev", Main.starbankBot.getAvatarURL()).build();
    }

    /*
       Getters
     */

    String getOwnerId() {
        return ownerId;
    }

    int getAmount() {
        return amount;
    }

    GuildConfiguration getConfig() {
        return config;
    }

    String getMessageId() {
        return message.getId();
    }
}
