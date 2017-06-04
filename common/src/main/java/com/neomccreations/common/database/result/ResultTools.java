package com.neomccreations.common.database.result;

import com.neomccreations.common.database.annotation.Column;
import com.neomccreations.common.database.annotation.InheritColumn;

import java.lang.reflect.Field;

/**
 * Holds a set of utilities for working
 * with/in {@link SQLResult}.
 *
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (7:14 PM)
 */
class ResultTools
{

    /**
     * Checks whether or not the provided
     * field is to be included in copying
     * over values by our system.
     *
     * @param field The field
     * @return Yes or no
     */
    static boolean canUseField(Field field)
    {
        return field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(InheritColumn.class);
    }

    /**
     * Looks into the annotations on the
     * provided field for the name of
     * whatever column is in conjunction to it.
     *
     * <p>
     * I do assume that you've already verified this
     * field is fit for this sort of thing. If
     * it really isn't a {@link NullPointerException}
     * will be thrown.
     *
     * @param field The field
     * @return The column name
     *
     * @see #canUseField(Field) Verification that this will work
     */
    static String columnNameFromField(Field field)
    {
        return field.isAnnotationPresent(InheritColumn.class) ? field.getName() : field.getAnnotation(Column.class).value();
    }

}
