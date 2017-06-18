package net.neogamesmc.common.database.mutate;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles the transformation of some
 * item into what will be written to
 * a {@link java.sql.PreparedStatement}.
 *
 * @param <T> Type of what we're encoding
 *
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (9:04 PM)
 */
public interface To<T>
{

    /**
     * For the provided item write it
     * to the SQL statement.
     *
     * @param data What we're working with
     * @param index Where in the statement we're writing to
     * @param statement The statement
     * @throws SQLException In the event something goes wrong
     */
    void to(T data, int index, PreparedStatement statement) throws SQLException;

}
