package net.neogamesmc.common.redis.api;

import com.google.gson.JsonObject;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;

/**
 * Represents a packet of data sent over
 * our shared Redis channels.
 *
 * @author Ben (OutdatedVersion)
 * @since Mar/24/2017 (3:42 PM)
 */
public interface Payload
{

    /**
     * Turns this payload into a
     * JSON object that we may then
     * send over the provided channel
     *
     * @return the payload as JSON
     */
    JsonObject asJSON();

    /**
     * @return the Redis channel this payload
     *         is to be sent over
     *
     * @see RedisChannel
     */
    RedisChannel channel();

    /**
     * @param focus the {@code focus} of this payload
     * @return the object as a {@link String}
     */
    default String asString(String focus)
    {
        final JsonObject json = new JsonObject();

        json.addProperty("focus", focus);

        final JsonObject payload = this.asJSON();

        if (payload != null)
            json.add("payload", payload);

        return json.toString();
    }

    /**
     * @param redis the redis instance we're using
     * @return the redis instance that we just
     *         published this payload via
     */
    default RedisHandler publish(RedisHandler redis)
    {
        redis.publish(this.channel().channel, this);
        return redis;
    }

}
