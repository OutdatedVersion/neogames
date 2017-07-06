package net.neogamesmc.common.redis.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/24/2017 (2:40 PM)
 */
@Target  ( ElementType.TYPE )
@Retention ( RetentionPolicy.RUNTIME )
public @interface Focus
{

    /**
     * @return what the provided Redis hook
     *         is handling; corresponds to the
     *         {@code focus} field in received payload
     */
    String value();

}
