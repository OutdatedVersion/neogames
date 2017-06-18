package net.neogamesmc.common.database.mutate;

import com.google.common.collect.Maps;
import net.neogamesmc.common.reference.Role;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Contains default {@link Mutators}.
 *
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (9:14 PM)
 */
public class Mutators
{

    /**
     * Turns an undashed string representation of {@link UUID} into an actual UUID.
     */
    private static Function<String, UUID> UNDASHED_UUID_PARSER = val -> new UUID(Long.parseUnsignedLong(val.substring(0, 16), 16),
                                                                                 Long.parseUnsignedLong(val.substring(16), 16));

    /**
     * A relation of classes to their {@link Mutator}.
     */
    private static final Map<Class, Mutator> DEFAULT = Maps.newHashMap();

    /**
     * Grabs a {@link Mutator} for the provided class.
     *
     * @param clazz The class
     * @return The {@link Mutator} or {@code null}
     */
    public static Mutator of(Class clazz)
    {
        return DEFAULT.get(clazz);
    }

    /**
     * Checks whether the provided class
     * has a {@link Mutator} associated with it.
     *
     * @param clazz The class
     * @return Yes or no
     */
    public static boolean hasMutator(Class clazz)
    {
        return DEFAULT.containsKey(clazz);
    }

    // populate
    static
    {
        DEFAULT.put(String.class, new Mutator<String>()
        {
            @Override
            public String from(String fieldName, ResultSet result) throws SQLException
            {
                return result.getString(fieldName);
            }

            @Override
            public void to(String data, int index, PreparedStatement statement) throws SQLException
            {
                statement.setString(index, data);
            }
        });

        DEFAULT.put(int.class, new Mutator<Integer>()
        {
            @Override
            public void to(Integer data, int index, PreparedStatement statement) throws SQLException
            {
                statement.setInt(index, data);
            }

            @Override
            public Integer from(String fieldName, ResultSet result) throws SQLException
            {
                return result.getInt(fieldName);
            }
        });

        DEFAULT.put(Instant.class, new Mutator<Instant>()
        {
            @Override
            public void to(Instant data, int index, PreparedStatement statement) throws SQLException
            {
                statement.setTimestamp(index, Timestamp.from(data));
            }

            @Override
            public Instant from(String fieldName, ResultSet result) throws SQLException
            {
                return result.getTimestamp(fieldName).toInstant();
            }
        });

        DEFAULT.put(UUID.class, new Mutator<UUID>()
        {
            @Override
            public void to(UUID data, int index, PreparedStatement statement) throws SQLException
            {
                statement.setString(index, data.toString().replaceAll("-", ""));
            }

            @Override
            public UUID from(String fieldName, ResultSet result) throws SQLException
            {
                return UNDASHED_UUID_PARSER.apply(result.getString(fieldName));
            }
        });

        DEFAULT.put(Role.class, new Mutator<Role>()
        {
            @Override
            public void to(Role data, int index, PreparedStatement statement) throws SQLException
            {
                statement.setString(index, data.name());
            }

            @Override
            public Role from(String fieldName, ResultSet result) throws SQLException
            {
                return Role.valueOf(result.getString(fieldName));
            }
        });
    }

}
