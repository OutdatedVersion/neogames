package com.neomccreations.common.database.mutate;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles the transformation of some
 * item within a SQL {@link ResultSet}
 * into the type we desire.
 *
 * @param <T> Type of what we're decoding
 *
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (9:04 PM)
 */
public interface From<T>
{

    /**
     * For the item contained at the
     * column name, grab it from the
     * result and transform it into
     * what it is we wanted.
     *
     * @param fieldName The column name
     * @param result The result
     * @return The item
     * @throws SQLException In the event something goes wrong
     */
    T from(String fieldName, ResultSet result) throws SQLException;
    
}
