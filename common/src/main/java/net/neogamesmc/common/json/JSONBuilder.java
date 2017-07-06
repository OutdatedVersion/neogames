package net.neogamesmc.common.json;

import com.google.gson.JsonObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collections;

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
    private JSONObject json;

    /**
     * Create a new builder.
     */
    public JSONBuilder()
    {
        this.json = new JSONObject();
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
    public JSONBuilder add(String key, Object val)
    {
        json.put(key, val);
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
        final JSONArray array = new JSONArray();
        Collections.addAll(array, elements);

        json.put(key, array);
        return this;
    }

    /**
     * Finish up this builder.
     *
     * @return The underlying JSON object
     *         we've been working with.
     */
    public JSONObject done()
    {
        return json;
    }

}
