package net.vadamdev.starbankbot.language;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.vadamdev.starbankbot.Main;

import java.util.Locale;

/**
 * @author VadamDev
 * @since 30/12/2023
 */
public enum Lang {
    EN_US("English", Emoji.fromUnicode("\uD83C\uDDEC\uD83C\uDDE7"), Locale.US),
    FR_FR("Français", Emoji.fromUnicode("\uD83C\uDDEB\uD83C\uDDF7"), Locale.FRENCH);

    private final String displayName;
    private final Emoji flag;
    private final Locale locale;

    Lang(String displayName, Emoji flag, Locale locale) {
        this.displayName = displayName;
        this.flag = flag;
        this.locale = locale;
    }

    public String localize(String unlocalizedName) {
        return Main.starbankBot.getLanguageManager().localize(this, unlocalizedName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Emoji getFlag() {
        return flag;
    }

    public Locale toLocale() {
        return locale;
    }

    public SelectOption toSelectOption() {
        return SelectOption.of(displayName, name()).withEmoji(flag);
    }

    public static StringSelectMenu.Builder createSelectionMenu() {
        final StringSelectMenu.Builder selectMenu = StringSelectMenu.create("StarBank-LanguageSelectMenu");

        for(Lang lang : values())
            selectMenu.addOptions(lang.toSelectOption());

        return selectMenu;
    }
}
