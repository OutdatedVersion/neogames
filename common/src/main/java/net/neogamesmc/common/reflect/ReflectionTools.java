package net.neogamesmc.common.reflect;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;

/**
 * Utilities relating to the usage of Java reflection.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/29/2017 (2:33 AM)
 */
public class ReflectionTools
{

    /**
     * Returns the name of a field.
     *
     * @param field The field
     * @return The name of the field
     */
    public static String nameFromField(Field field)
    {
        return field.isAnnotationPresent(SerializedName.class)
                   ? field.getAnnotation(SerializedName.class).value()
                   : field.getName();
    }

}
