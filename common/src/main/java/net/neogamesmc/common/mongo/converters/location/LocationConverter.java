package net.neogamesmc.common.mongo.converters.location;

import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/23/2017 (10:51 PM)
 */
public class LocationConverter extends TypeConverter
{

    public LocationConverter()
    {

    }

    @Override
    public Object decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo)
    {

    }

    @Override
    public Object encode(Object value, MappedField optionalExtraInfo)
    {

    }

    /**
     * Represents a Bukkit location.
     */
    public static class Location
    {

        public int x, y, z;

        public float pitch, yaw;

    }

}
