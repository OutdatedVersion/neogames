package net.neogamesmc.core.command.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben (OutdatedVersion)
 * @since Feb/28/2017 (6:01 PM)
 */
@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.METHOD )
public @interface Command
{

    /**
     * @return what the player may
     *         use to run this command
     */
    String[] executor();

    /**
     * @return if the player doesn't have
     *         permission to run the command
     *         and this is true a help message
     *         will be sent
     */
    boolean hidden() default false;

}
