package net.neogamesmc.common.redis.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method
 * handles a specific payload type.
 *
 * @author Ben (OutdatedVersion)
 * @since Mar/25/2017 (3:07 PM)
 */
@Target ( ElementType.METHOD )
@Retention ( RetentionPolicy.RUNTIME )
public @interface HandlesType
{

    /**
     * @return the class of the payload
     *         that the hook this is
     *         annotating is for
     */
    Class<? extends Payload> value();

}
