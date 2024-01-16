package net.vadamdev.starbankbot.commands.settings.menu;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.vadamdev.jdautils.smart.entities.SmartButton;
import net.vadamdev.jdautils.smart.entities.SmartStringSelectMenu;
import net.vadamdev.jdautils.smart.messages.MessageContent;
import net.vadamdev.starbankbot.Main;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.language.Lang;
import net.vadamdev.starbankbot.utils.StarbankEmbed;

/**
 * @author VadamDev
 * @since 12/01/2024
 */
public class SettingsLanguageMenu extends AbstractSettingsMenu {
    @Override
    public void init(Guild guild, MessageContent contents) {
        final GuildConfiguration config = Main.starbankBot.getGuildConfigManager().getOrDefault(guild);
        final Lang lang = config.getLang();

        /*
           Embed
         */

        contents.setEmbed(new StarbankEmbed()
                .setTitle("Star Bank - " + lang.localize("settings.name"))
                .setDescription(lang.localize("settings.languageMenu.description"))
                .setColor(StarbankEmbed.CONFIG_COLOR)
                .build());

        /*
           Components
         */

        contents.addComponents(
                SmartStringSelectMenu.of(Lang.createSelectionMenu().setDefaultOptions(lang.toSelectOption()).build(), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    final Lang newLang = Lang.valueOf(event.getValues().get(0));

                    setConfigValue(config, "LANG", newLang.name());

                    replySuccessMessage(event, newLang);
                    SettingsMainMenu.SETTINGS_MAIN_MENU.open(event.getMessage());
                })
        );

        contents.addComponents(
                SmartButton.of(Button.secondary("StarBank-Settings-MainMenu", lang.localize("settings.back")), event -> {
                    if(!canUse(event.getMember()))
                        return;

                    event.deferEdit().queue();
                    SettingsMainMenu.SETTINGS_MAIN_MENU.open(event.getMessage());
                })
        );
    }
}
