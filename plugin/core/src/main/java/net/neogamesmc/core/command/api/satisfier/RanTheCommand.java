package net.neogamesmc.core.command.api.satisfier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ben (OutdatedVersion)
 * @since Mar/21/2017 (11:33 AM)
 */
@Target ( ElementType.PARAMETER )
@Retention ( RetentionPolicy.RUNTIME )
public @interface RanTheCommand
{

}
