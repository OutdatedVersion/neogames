package com.neomccreations.common.config;

import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.neomccreations.common.reference.Paths;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Function;

/**
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (9:52 PM)
 */
@Singleton
public class ConfigurationProvider
{

    /**
     * Takes in file names and appends our data type to it.
     */
    private static final Function<String, String> FILE_FORMATTER = val -> val + ".json";

    /**
     * Utility {@link Gson} instance.
     */
    private static final Gson GSON = new Gson();

    /**
     * Read the contents of a file at the
     * specified path and return it as some
     * code-friendly format.
     *
     * @param filePath Path to the file relative to {@link Paths#CONFIG}.
     * @param clazz Class of the code representation for the config.
     * @param <T> Type parameter for that class.
     * @return The easy-to-use code format.
     */
    public <T> T read(String filePath, Class<T> clazz)
    {
        try (BufferedReader reader = Files.newBufferedReader(Paths.CONFIG.fileAt(FILE_FORMATTER.apply(filePath)).toPath()))
        {
            return GSON.fromJson(reader, clazz);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Unable to read configuration file.", ex);
        }
    }

}
