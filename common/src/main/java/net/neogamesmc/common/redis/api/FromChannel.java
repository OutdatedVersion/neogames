package net.neogamesmc.common.redis.api;

import net.neogamesmc.common.redis.RedisChannel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element takes
 * in a {@link Payload} from the defined channel.
 *
 * @author Ben (OutdatedVersion)
 * @since Mar/24/2017 (2:47 PM)
 */
@Target ( ElementType.METHOD )
@Retention ( RetentionPolicy.RUNTIME )
public @interface FromChannel
{

    /**
     * @return the specific channel that the
     *         hook we're using is listening to
     */
    RedisChannel value();

}
