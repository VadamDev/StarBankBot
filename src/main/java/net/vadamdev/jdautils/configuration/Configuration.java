package net.vadamdev.jdautils.configuration;

import org.simpleyaml.configuration.file.YamlFile;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Represents a configuration file.
 * <br>Every primitive field annotated with {@link ConfigValue} will be configurable in the associated yml file.
 *
 * @author VadamDev
 * @since 18/10/2022
 */
public class Configuration {
    protected final YamlFile yamlFile;

    public Configuration(YamlFile yamlFile) {
        this.yamlFile = yamlFile;
    }

    public Configuration(String path) {
        this(new YamlFile(path));
    }

    public Configuration(File file) {
        this(new YamlFile(file));
    }

    /**
     * Change the provided value in the yml and field.
     * <br>Use the save() function to save the changes in the yml file.
     *
     * @param name Field name
     * @param value New value
     */
    public void setValue(@Nonnull String name, Object value) {
        try {
            final Field field = getClass().getField(name);

            final ConfigValue annotation = field.getAnnotation(ConfigValue.class);
            if(annotation == null)
                return;

            field.set(this, value);
            yamlFile.set(annotation.path(), value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the provided fieldName exists.
     *
     * @param fieldName Field name to check
     * @return True if the provided field name is a field
     */
    public boolean hasField(@Nonnull String fieldName) {
        try {
            return Arrays.stream(getClass().getField(fieldName).getAnnotations())
                    .anyMatch(ConfigValue.class::isInstance);
        } catch (NoSuchFieldException ignored) {
            return false;
        }
    }

    /**
     * Save current changes in the yml file.
     */
    public void save() throws IOException {
        yamlFile.save();
    }

    public YamlFile getYamlFile() {
        return yamlFile;
    }
}
