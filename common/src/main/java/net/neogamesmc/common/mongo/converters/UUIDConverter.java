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

    /**
     * Parse {@link UUID}s properly
     */
    public UUIDConverter()
    {
        super(UUID.class);
    }

    /**
     * Parse the retrieved {@link String} from Mongo.
     *
     * @param target Target class
     * @param obj The db
     * @param info ignored
     * @return The UUID
     */
    @Override
    public Object decode(Class<?> target, Object obj, MappedField info)
    {
        return obj == null ? null : UNDASHED_UUID_PARSER.apply((String) obj);
    }

    /**
     * Write the provided UUID into a document as a String.
     *
     * @param val The UUID
     * @param info ignored
     * @return The String
     */
    @Override
    public Object encode(Object val, MappedField info)
    {
        return val == null ? null : val.toString().replaceAll("-", "");
    }

}
