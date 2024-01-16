package net.vadamdev.starbankbot.transaction;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.language.Lang;

/**
 * @author VadamDev
 * @since 01/01/2024
 */
public enum DistributionMode {
    DEFAULT("Default", "distributionMode.description.DEFAULT.a", Emoji.fromUnicode("\uD83D\uDEAB"), (amount, userCount, config) -> {
        return userCount == 0 ? new double[] { amount, 0 } : new double[] { (double) amount / userCount, 0 };
    }),

    PERCENTAGE("Percentage", "distributionMode.description.PERCENTAGE.a", Emoji.fromUnicode("\uD83D\uDCC8"), (amount, userCount, config) -> {
        final double bot = (double) config.TRANSACTION_PERCENTAGE / 100 * amount;

        return userCount == 0 ? new double[] { amount - bot, bot } : new double[] { (amount - bot) / userCount, bot };
    }),

    COUNT_AS_USER("Count As User", "distributionMode.description.COUNT_AS_USER.a", Emoji.fromUnicode("\uD83D\uDC65"), (amount, userCount, config) -> {
        final double result = (double) amount / (userCount + 1);

        return userCount == 0 ? new double[] { 0, result } : new double[] { result, result };
    });

    private final String displayName, description;
    private final Emoji icon;
    private final DistributionFunction function;

    DistributionMode(String displayName, String description, Emoji icon, DistributionFunction function) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.function = function;
    }

    public double[] distribute(int amount, int userCount, GuildConfiguration config) {
        return function.distribute(amount, userCount, config);
    }

    public String getDisplayName() {
        return displayName;
    }

    public SelectOption toSelectOption(Lang lang) {
        return SelectOption.of(displayName, name()).withDescription(lang.localize(description)).withEmoji(icon);
    }

    public static StringSelectMenu.Builder createSelectionMenu(Lang lang) {
        final StringSelectMenu.Builder selectMenu = StringSelectMenu.create("StarBank-TransactionSelectMenu");

        for(DistributionMode mode : values())
            selectMenu.addOptions(mode.toSelectOption(lang));

        return selectMenu;
    }

    @FunctionalInterface
    private interface DistributionFunction {
        double[] distribute(int amount, int userAmount, GuildConfiguration config);
    }
}
