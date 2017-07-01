package net.neogamesmc.common.json;

import com.google.gson.JsonArray;
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
     * Create a new builder.
     */
    public JSONBuilder()
    {
        this.json = new JsonObject();
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
     * Store a new item array in this object.
     *
     * @param key The key to store it at
     * @param elements The elements to store
     * @return This builder, for chaining
     */
    public JSONBuilder add(String key, String... elements)
    {
        final JsonArray array = new JsonArray();

        for (String element : elements)
            array.add(element);

        json.add(key, array);
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
