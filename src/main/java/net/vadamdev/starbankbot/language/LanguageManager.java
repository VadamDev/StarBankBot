package net.vadamdev.starbankbot.language;

import net.vadamdev.starbankbot.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VadamDev
 * @since 01/01/2024
 */
public class LanguageManager {
    private final Map<Lang, Map<String, String>> languages;
    private final Logger logger;

    public LanguageManager(Logger logger) {
        this.languages = new EnumMap<>(Lang.class);
        this.logger = logger;

        logger.info("Looking for languages...");

        final File directory;
        try {
            directory = new File(getClass().getResource("/langs").toURI());
        } catch (URISyntaxException e) {
            logger.error("-> An error occurred during languages loading:");
            e.printStackTrace();
            return;
        }

        for (File file : directory.listFiles()) {
            final String langName = Utils.stripExtension(file.getName()).toUpperCase();
            final Lang lang;

            try {
                lang = Lang.valueOf(langName);
            }catch (Exception ignored) {
                logger.warn("\"" + langName + "\" is an unknown language, skipped loading");
                continue;
            }

            languages.put(lang, readLangData(file));
        }

        logger.info("-> Loaded " + languages.size() + " languages !");
    }

    protected String localize(Lang lang, String unlocalizedName) {
        return languages.containsKey(lang) ? languages.get(lang).getOrDefault(unlocalizedName, tryFallBack(unlocalizedName)) : tryFallBack(unlocalizedName);
    }

    private String tryFallBack(String unlocalizedName) {
        return languages.containsKey(Lang.EN_US) ? languages.get(Lang.EN_US).getOrDefault(unlocalizedName, unlocalizedName) : unlocalizedName;
    }

    private Map<String, String> readLangData(File file) {
        final Map<String, String> data = new HashMap<>();

        try {
            final JSONObject jsonObject = (JSONObject) Utils.parseFile(file);

            for (Object o : jsonObject.entrySet()) {
                Map.Entry<String, ?> entry = (Map.Entry<String, ?>) o;

                if(entry.getValue() instanceof String)
                    data.put(entry.getKey(), (String) entry.getValue());
                else if(entry.getValue() instanceof List) {
                    final StringBuilder builder = new StringBuilder();
                    for(String str : (List<String>) entry.getValue())
                        builder.append(str + "\n");

                    data.put(entry.getKey(), builder.toString());
                }
            }
        }catch(ParseException | IOException e) {
            logger.error("Error occurred during the loading of " + file.getName() + ": " + e);
        }

        return data;
    }
}
