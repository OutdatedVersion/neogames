package com.neomccreations.common.database.operation;

import com.neomccreations.common.database.Database;
import com.neomccreations.common.database.mutate.Mutators;
import com.neomccreations.common.database.result.SQLResult;

import java.sql.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A continence tool for working with
 * SQL data-fetching queries; Mostly
 * the {@code SELECT} command.
 *
 * <p>
 * Typical backing cycle is as follows:
 *
 * 1. Query setup with object creation
 * 2. Data for the statement stored via {@link #data(Object...)}
 * 3. Statement execution in a fashion desired by {@link #sync(Database)} or {@link #async(Database)}
 *
 * @author Ben (OutdatedVersion)
 * @since May/20/2017 (1:50 AM)
 */
public class FetchOperation implements Callable<SQLResult>
{

    /**
     * The SQL query behind this operation.
     */
    private final String sql;

    /**
     * The data provided to the query. (statement)
     */
    private Object[] data;

    /**
     * Let's us know that we don't require
     * any value on {@link #data}.
     */
    private boolean requireNoData;

    /**
     * Database instance for working with data.
     *
     * <p>
     * This must be assigned by one of the exposed
     * execution methods.
     *
     * @see #sync(Database) Value assigned
     * @see #async(Database) Value assigned
     */
    private Database database;

    /**
     * @param sql See {@link #sql}
     */
    public FetchOperation(String sql)
    {
        this.sql = sql;
    }

    /**
     * Sets the backing data for this query.
     *
     * <p>
     * Data parameters in the query will be satisfied
     * with this data. As in, the {@code ?}s in the query.
     *
     * <p>
     * Raw data may be mapped to new values during statement
     * creation.
     *
     * @param data The data
     * @return This operation for chaining
     */
    public FetchOperation data(Object... data)
    {
        this.data = checkNotNull(data, "Data provided must be non-null");
        return this;
    }

    /**
     * Sometimes a query will not require any
     * sort of parameters. Though we do still
     * need some sort of indicator that we don't
     * actually need that for the execution.
     *
     * @return This operation for chaining
     */
    public FetchOperation noData()
    {
        this.requireNoData = true;
        return this;
    }

    @Override
    public SQLResult call() throws Exception
    {
        checkNotNull(this.database, "Do NOT call this method directly. Use #sync or #async");

        if (!requireNoData)
            checkNotNull(this.data, "You must setup the backing data first");

        try
        (
            Connection connection = this.database.reserve();
            PreparedStatement statement = FetchOperation.statement(connection, this.sql, this.data);
            ResultSet result = statement.executeQuery()
        )
        {
            return new SQLResult(result);
        }
    }

    /**
     * Performs the SQL query in a thread-blocking fashion.
     *
     * @param database Database instance
     * @return The data from the {@link java.sql.ResultSet}
     * @throws Exception In the case something goes wrong
     */
    public SQLResult sync(Database database) throws Exception
    {
        this.database = database;
        return call();
    }

    /**
     * Performs the SQL query on our {@link Database}'s
     * task queue. Data is returned wrapped in a {@link Future}.
     *
     * @param database Database instance
     * @return Transformed & wrapped data from the {@link java.sql.ResultSet}
     * @throws Exception In the case that something goes wrong
     */
    public Future<SQLResult> async(Database database) throws Exception
    {
        this.database = database;
        return database.submitTask(this);
    }

    /**
     * To keep everything wrapped in a single
     * try-with-resources block we keep have
     * this utility method.
     *
     * @param connection The connection
     * @param sql Raw SQL
     * @param data Data for the SQL
     * @return The statement
     * @throws SQLException In case something goes wrong
     */
    @SuppressWarnings ( "unchecked" )
    private static PreparedStatement statement(Connection connection, String sql, Object[] data) throws SQLException
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
