package net.vadamdev.starbankbot.transaction;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.language.Lang;
import net.vadamdev.starbankbot.utils.StarbankEmbed;
import net.vadamdev.starbankbot.utils.Utils;

import java.text.NumberFormat;
import java.util.*;
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

    private final Map<String, Data> users;

    private Message message;

    public Transaction(Member owner, int amount, boolean ignoreConfig, GuildConfiguration config, IReplyCallback callback) {
        this.ownerId = owner.getId();
        this.amount = amount;
        this.ignoreConfig = ignoreConfig;
        this.config = config;

        this.users = new HashMap<>();

        final GuildVoiceState voiceState = owner.getVoiceState();
        if(voiceState.inAudioChannel())
            voiceState.getChannel().getMembers().forEach(member -> users.putIfAbsent(member.getId(), new Data(member.getUser().getEffectiveName())));
        else
            users.put(ownerId, new Data(owner.getUser().getEffectiveName()));

        callback.replyEmbeds(createTransactionEmbed())
                .setActionRow(
                        Button.success("StarBank-Transaction-Use", Emoji.fromUnicode("✅")),
                        Button.danger("StarBank-Transaction-Close", Emoji.fromUnicode("\uD83D\uDD12")),
                        Button.secondary("StarBank-Transaction-Add", Emoji.fromUnicode("➕"))
                ).queue(a -> a.retrieveOriginal().queue(message -> this.message = message));
    }

    public void addUsers(Collection<User> users) {
        users.forEach(user -> this.users.putIfAbsent(user.getId(), new Data(user.getEffectiveName())));
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

            final Data data = users.get(userId);
            data.isTaking = !data.isTaking;

            refreshMessage();
        }else
            callback.replyEmbeds(new StarbankEmbed()
                    .setDescription(config.getLang().localize("transaction.error.not_user"))
                    .setColor(StarbankEmbed.ERROR_COLOR).build())
                    .setEphemeral(true).queue();
    }

    protected void computeCloseButton(String userId, IReplyCallback callback) {
        if(!checkOwner(userId, callback))
            return;

        Main.starbankBot.getTransactionManager().removeTransaction(this);
        message.editMessageComponents(message.getActionRows().stream().map(ActionRow::asDisabled).collect(Collectors.toList())).queue();

        callback.replyEmbeds(createEndTransactionEmbed()).queue();
    }

    protected void computeAddButton(String userId, IReplyCallback callback) {
        if(!checkOwner(userId, callback))
            return;

        callback.replyEmbeds(new StarbankEmbed()
                .setTitle("Star Bank - " + amount + " aUEC")
                .setDescription(config.getLang().localize("transaction.button.add.message"))
                .setColor(StarbankEmbed.CONFIG_COLOR).build()
        ).setEphemeral(true).setComponents(
                ActionRow.of(EntitySelectMenu.create("StarBank-Transaction-ChannelSelectMenu_" + message.getId(), EntitySelectMenu.SelectTarget.CHANNEL)
                        .setChannelTypes(ChannelType.VOICE)
                        .setRequiredRange(1, 3)
                        .build()),
                ActionRow.of(EntitySelectMenu.create("StarBank-Transaction-UserSelectMenu_" + message.getId(), EntitySelectMenu.SelectTarget.USER)
                        .setRequiredRange(1, 5)
                        .build())
        ).queue();
    }

    private boolean checkOwner(String userId, IReplyCallback callback) {
        if(userId.equals(ownerId))
            return true;

        callback.replyEmbeds(new StarbankEmbed()
                .setDescription(config.getLang().localize("transaction.error.not_owner"))
                .setColor(StarbankEmbed.ERROR_COLOR).build()).setEphemeral(true).queue();

        return false;
    }

    /*
       Embeds Creation
     */

    private MessageEmbed createTransactionEmbed() {
        final Lang lang = config.getLang();

        final StringBuilder description = new StringBuilder(
                "> " + lang.localize("distributionMode.description." + (ignoreConfig ? DistributionMode.DEFAULT : config.getDistributionMode()).name() + ".b", str -> str.replace("%amount%", String.valueOf(amount)).replace("%percentage%", String.valueOf(config.TRANSACTION_PERCENTAGE))) + "\n" +
                "\n" +
                lang.localize("transaction.header") + "\n"
        );

        users.entrySet().stream()
                .sorted(Comparator.comparing(a -> a.getValue().username))
                .forEachOrdered(entry -> description.append("> (" + Utils.displayBoolean(entry.getValue().isTaking) + ") - <@" + entry.getKey() + ">\n"));

        description.append("\n" + lang.localize("transaction.footer"));

        return new StarbankEmbed()
                .setTitle("Star Bank - " + amount + " aUEC")
                .setDescription(description.toString())
                .setColor(StarbankEmbed.NEUTRAL_COLOR)
                .build();
    }

    private MessageEmbed createEndTransactionEmbed() {
        final Lang lang = config.getLang();
        final List<String> members = users.entrySet().stream()
                .filter(entry -> entry.getValue().isTaking)
                .sorted(Comparator.comparing(entry -> entry.getValue().username))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        final StringBuilder description = new StringBuilder(lang.localize("transaction.result.header", str -> str
                .replace("%member_count%", String.valueOf(members.size()))) + "\n");

        members.forEach(userId -> description.append("> <@" + userId + ">\n"));

        final double[] result = (ignoreConfig ? DistributionMode.DEFAULT : config.getDistributionMode()).distribute(amount, members.size(), config);
        final NumberFormat numberFormat = NumberFormat.getInstance(lang.toLocale());

        final double member = Math.floor(result[0] - (0.005 * result[0]));
        final double bot = Math.floor(result[1] - (0.005 * result[1]));

        description.append("\n" + lang.localize("transaction.result.footer", str -> str
                .replace("%member%", numberFormat.format(member))
                .replace("%bot%", numberFormat.format(bot))));

        return new StarbankEmbed()
                .setTitle("Star Bank - " + amount + " aUEC")
                .setDescription(description.toString())
                .setColor(StarbankEmbed.SUCCESS_COLOR).build();
    }

    /*
       Getters
     */

    protected String getOwnerId() {
        return ownerId;
    }

    protected String getMessageId() {
        return message.getId();
    }

    private static class Data {
        private final String username;
        private boolean isTaking;

        private Data(String username) {
            this.username = username;
            this.isTaking = false;
        }
    }
}
