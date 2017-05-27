package com.neomccreations.common.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated class
 * will be found in a certain table.
 *
 * @author Ben (OutdatedVersion)
 * @since May/25/2017 (7:36 PM)
 */
@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.TYPE )
public @interface Table
{

    /**
     * @return The table that the annotated element
     *         is held in.
     */
    String value();

}

