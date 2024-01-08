package net.vadamdev.starbankbot.utils;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author VadamDev
 * @since 05/01/2024
 */
public final class Utils {
    private Utils() {}

    public static String stripExtension(String fileName) {
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    public static String formatBoolean(boolean b) {
        return b ? "✅" : "❌";
    }

    @Nonnull
    public static File initDirectory(String pathName) {
        final File file = new File(pathName);
        if(!file.exists())
            file.mkdirs();

        return file;
    }

    public static Object parseFile(InputStream in) throws ParseException, IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        final StringBuilder jsonString = new StringBuilder();

        String line;
        while((line = reader.readLine()) != null)
            jsonString.append(line);

        reader.close();

        if(jsonString.toString().equals(""))
            return new JSONArray();

        return new JSONParser().parse(jsonString.toString());
    }

    public static Object parseFile(File file) throws ParseException, IOException {
        return parseFile(new FileInputStream(file));
    }
}
