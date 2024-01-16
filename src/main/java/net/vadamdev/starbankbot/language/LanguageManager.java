package net.vadamdev.starbankbot.language;

import net.vadamdev.starbankbot.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

        for (Lang lang : Lang.values()) {
            String path = "/langs/" + lang.name().toLowerCase() + ".json";
            InputStream stream = getClass().getResourceAsStream(path);

            if(stream == null) {
                logger.warn("Unable to find \"" + path + "\", skipped loading");
                continue;
            }

            languages.put(lang, readLangData(stream, lang.name()));
        }

        logger.info("-> Loaded " + languages.size() + " languages !");
    }

    protected String localize(Lang lang, String unlocalizedName) {
        return languages.getOrDefault(lang, languages.getOrDefault(Lang.EN_US, Collections.emptyMap())).getOrDefault(unlocalizedName, unlocalizedName);
    }

    private Map<String, String> readLangData(InputStream stream, String path) {
        final Map<String, String> data = new HashMap<>();

        try {
            final JSONObject jsonObject = (JSONObject) Utils.parseFile(stream);

            for (Object o : jsonObject.entrySet()) {
                final Map.Entry<String, ?> entry = (Map.Entry<String, ?>) o;

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
            logger.error("Error occurred during the loading of " + path + ": " + e);
        }

        return data;
    }
}
