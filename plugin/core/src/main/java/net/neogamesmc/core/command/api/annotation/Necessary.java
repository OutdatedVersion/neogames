package net.neogamesmc.core.command.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/22/2017 (10:16 AM)
 */
@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.PARAMETER )
public @interface Necessary
{

    /**
     * @return if not present send this
     */
    String value();

}
