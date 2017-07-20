package net.neogamesmc.common.mongo.converters;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.util.UUID;
import java.util.function.Function;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (1:24 AM)
 */
public class UUIDConverter extends TypeConverter implements SimpleValueConverter
{

    /**
     * Turns an undashed string representation of {@link UUID} into an actual UUID.
     */
    private static Function<String, UUID> UNDASHED_UUID_PARSER = val -> new UUID(Long.parseUnsignedLong(val.substring(0, 16), 16),
                                                                                 Long.parseUnsignedLong(val.substring(16), 16));

    public UUIDConverter()
    {
        super(UUID.class);
    }

    @Override
    public Object decode(Class<?> target, Object fromDBObject, MappedField optionalExtraInfo)
    {
        return fromDBObject == null ? null : UNDASHED_UUID_PARSER.apply((String) fromDBObject);
    }

    @Override
    public Object encode(Object value, MappedField optionalExtraInfo)
    {
        return value == null ? null : value.toString().replaceAll("-", "");
    }

}
