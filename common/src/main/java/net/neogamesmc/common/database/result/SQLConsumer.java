package net.neogamesmc.common.database.result;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Works similar to a {@link java.util.function.Consumer},
 * but allows for the thrown {@link SQLException}s that the
 * operations a set may run has.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/28/2017 (5:22 PM)
 */
public interface SQLConsumer
{

    /**
     * Use the provided {@link ResultSet} for stuff.
     *
     * @param set The set
     * @throws SQLException In the event that something goes wrong
     */
    void accept(ResultSet set) throws SQLException;

}
