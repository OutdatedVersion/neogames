package com.neomccreations.common.database.operation;

import com.neomccreations.common.database.Database;
import com.neomccreations.common.database.mutate.Mutators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Represents some sort of interaction with
 * one of our databases. Mostly we'll wrap
 * up some request for data here or such.
 *
 * @param <R> Return type of our methods
 *
 * @author Ben (OutdatedVersion)
 * @since May/25/2017 (6:19 PM)
 */
public interface Operation<R> extends Callable<R>
{

    /**
     * Executes this operation in a thread-blocking manor.
     *
     * @param database An instance of our database
     * @return The result of type {@code T}
     * @throws Exception In the event that something goes wrong
     */
    R sync(Database database) throws Exception;

    /**
     * Executes this operation on our {@link Database}'s
     * task queue. A result will be returned in a
     * non-blocking {@link Future}.
     *
     * @param database An instance of our database
     * @return The result held in a {@link Future}
     * @throws Exception In the event that something goes wrong
     */
    Future<R> async(Database database) throws Exception;

    /**
     * To keep everything wrapped in a single
     * try-with-resources block we keep have
     * this utility method.
     *
     * @param connection The connection
     * @param sql Raw SQL statement
     * @param data Data for the SQL
     * @return The statement
     * @throws SQLException In case something goes wrong
     */
    @SuppressWarnings ( "unchecked" )
    static PreparedStatement statement(Connection connection, String sql, Object[] data) throws SQLException
    {
        final PreparedStatement _statement = connection.prepareStatement(sql);

        if (data != null)
        {
            for (int i = 0; i < data.length; i++)
            {
                Object _obj = data[i];

                if (Mutators.hasMutator(_obj.getClass()))
                    Mutators.of(_obj.getClass()).to(_obj, i + 1, _statement);
                else
                    _statement.setObject(i + 1, _obj);
            }
        }

        return _statement;
    }

}
