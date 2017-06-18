package net.neogamesmc.common.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field
 * is to be assigned to a value held
 * by a {@link java.sql.ResultSet} matching
 * the provided name.
 *
 * @author Ben (OutdatedVersion)
 * @since May/20/2017 (2:10 AM)
 */
@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.FIELD )
public @interface Column
{

    /**
     * @return The SQL column's name.
     */
    String value();

}
