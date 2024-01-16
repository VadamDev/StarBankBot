package net.vadamdev.starbankbot.commands.settings.menu;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.vadamdev.jdautils.smart.messages.SmartMessageProvider;
import net.vadamdev.starbankbot.commands.settings.SettingsCommand;
import net.vadamdev.starbankbot.config.GuildConfiguration;
import net.vadamdev.starbankbot.language.Lang;
import net.vadamdev.starbankbot.utils.StarbankEmbed;

import java.io.IOException;

/**
 * @author VadamDev
 * @since 12/01/2024
 */
public abstract class AbstractSettingsMenu implements SmartMessageProvider {
    protected boolean canUse(Member member) {
        return member.hasPermission(SettingsCommand.SETTINGS_PERMISSION);
    }

    protected void replySuccessMessage(IReplyCallback callback, Lang lang) {
        callback.replyEmbeds(new StarbankEmbed()
                .setTitle("Star Bank - " + lang.localize("settings.name"))
                .setDescription(lang.localize("settings.changes.success"))
                .setColor(StarbankEmbed.SUCCESS_COLOR)
                .build()).setEphemeral(true).queue();
    }

    protected void setConfigValue(GuildConfiguration config, String name, Object value) {
        try {
            config.setValue(name, value);
            config.save();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
