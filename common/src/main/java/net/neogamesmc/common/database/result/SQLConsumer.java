package net.neogamesmc.common.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Execute the SQL result
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
