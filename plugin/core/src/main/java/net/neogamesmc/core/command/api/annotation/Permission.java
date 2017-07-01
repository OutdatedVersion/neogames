package net.neogamesmc.core.command.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/01/2017 (4:08 PM)
 */
@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.METHOD )
public @interface Permission
{

    /**
     * @return The permission node required
     *         to execute this command.
     */
    String value();

    /**
     * @return the message we send in case
     *         the player doesn't have the
     *         role
     */
    String note() default "DEFAULT_MESSAGE";

}
