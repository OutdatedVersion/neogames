package net.neogamesmc.common.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Let's us know that the annotated
 * field must be satisfied with the
 * elapsed time since the UNIX epoch.
 *
 * <p>
 * This time will be provided after
 * converting the timestamp at the
 * particular index in a {@link java.sql.ResultSet}
 * to the total quantity of milliseconds
 * passed since Jan/1/1970.
 *
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (6:44 PM)
 */
@Target ( ElementType.FIELD )
@Retention ( RetentionPolicy.RUNTIME )
public @interface EpochFromTimestamp { }
