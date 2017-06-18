package net.neogamesmc.common.database.api;

import net.neogamesmc.common.database.Database;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

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
public abstract class Operation<R> implements Callable<R>
{

    /**
     * Raw SQL behind this operation.
     */
    protected final String sql;

    /**
     * Data backing our SQL statement.
     */
    protected Object[] data;

    /**
     * Database instance for working with data.
     * <p>
     * This must be assigned by one of the exposed
     * execution methods.
     *
     * @see #sync(Database) Value assigned
     * @see #async(Database) Value assigned
     */
    protected Database database;

    /**
     * Class Constructor
     *
     * @param sql Raw SQL
     */
    public Operation(String sql)
    {
        this.sql = sql;
    }

    /**
     * Executes this operation in a thread-blocking manor.
     *
     * @param database An instance of our database
     * @return The result of type {@code T}
     * @throws Exception In the event that something goes wrong
     */
    public abstract R sync(Database database) throws Exception;

    /**
     * Executes this operation on our {@link Database}'s
     * task queue. A result will be returned in a
     * non-blocking {@link Future}.
     *
     * @param database An instance of our database
     * @return The result held in a {@link Future}
     * @throws Exception In the event that something goes wrong
     */
    public abstract Future<R> async(Database database) throws Exception;

    /**
     * Sets the backing data for this operation.
     * <p>
     * Data parameters in the query will be satisfied
     * with this data. As in, the {@code ?}s in the query.
     * <p>
     * Raw data may be mapped to new values during statement
     * creation.
     *
     * @param data The data, as a simple array
     * @return This operation
     */
    public abstract Operation data(Object... data);

    /**
     * Checks that we have the resources required
     * to execute this operation. Usually the first
     * statement within {@link #call()}.
     */
    protected void stateCheck()
    {
        checkNotNull(this.database, "Do NOT call this method directly. Use #sync or #async");
    }

}
