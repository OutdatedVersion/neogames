package net.neogamesmc.common.json;

import com.google.gson.JsonObject;

/**
 * Allows a chaining approach to creating {@link JsonObject}s.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (2:06 AM)
 */
public class JSONBuilder
{

    /**
     * The underlying object for this builder.
     */
    private JsonObject json;

    /**
     * Create a new JSON builder.
     *
     * @return A fresh builder
     */
    public static JSONBuilder create()
    {
        return new JSONBuilder();
    }

    /**
     * Store a new item into this object.
     * <p>
     * Takes in a {@link String}.
     *
     * @param key The key to store it at
     * @param val The value to store
     * @return This builder, for chaining
     */
    public JSONBuilder add(String key, String val)
    {
        json.addProperty(key, val);
        return this;
    }

    /**
     * Store a new item into this object.
     * <p>
     * Takes in some sort of number.
     *
     * @param key The key to store it at
     * @param val The value to store
     * @return This builder, for chaining
     */
    public JSONBuilder add(String key, Number val)
    {
        json.addProperty(key, val);
        return this;
    }

    /**
     * Finish up this builder.
     *
     * @return The underlying JSON object
     *         we've been working with.
     */
    public JsonObject done()
    {
        return json;
    }

}
