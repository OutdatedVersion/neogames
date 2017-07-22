package net.neogamesmc.common.redis.api;

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
     * @return the Redis channel this payload
     *         is to be sent over
     *
     * @see RedisChannel
     */
    RedisChannel channel();

    /**
     * Returns whether or not this payload does contain data.
     *
     * @return Yes or no
     */
    default boolean hasContent()
    {
        return true;
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
