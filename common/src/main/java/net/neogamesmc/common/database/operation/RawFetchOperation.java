package net.neogamesmc.common.database.operation;

import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.result.SQLConsumer;
import net.neogamesmc.common.database.api.Operation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Performs the same as {@link FetchOperation}, but
 * instead returns the underlying result.
 *
 * @see FetchOperation
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/28/2017 (5:12 PM)
 */
public class RawFetchOperation extends Operation<ResultSet>
{

    /**
     * Code to execute with the set.
     */
    private SQLConsumer task;

    /**
     * Let's us know that we don't require
     * any value on {@link #data}.
     */
    private boolean requireNoData = true;

    /**
     * {@inheritDoc}
     */
    public RawFetchOperation(String sql)
    {
        super(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RawFetchOperation data(Object... data)
    {
        this.requireNoData = false;
        this.data = data;
        return this;
    }

    /**
     * Set the underlying code to run with
     * this {@link ResultSet}.
     *
     * @param task The task
     * @return The operation to execute
     */
    public RawFetchOperation task(SQLConsumer task)
    {
        this.task = task;
        return this;
    }

    /**
     * Execute the query and transform it.
     *
     * @return Representation of the {@link ResultSet}
     * @throws Exception In the event that something goes wrong
     */
    @Override
    public ResultSet call() throws Exception
    {
        stateCheck();

        if (!requireNoData)
            checkNotNull(this.data, "You must setup the backing data first");

        try
        (
            Connection connection = this.database.reserve();
            PreparedStatement statement = OperationTools.statement(connection, this.sql, this.data)
        )
        {
            return statement.executeQuery();
        }
    }

    @Override
    public ResultSet sync(Database database) throws Exception
    {
        this.database = database;
        return call();
    }

    @Override
    public Future<ResultSet> async(Database database) throws Exception
    {
        this.database = database;
        return database.submitTask(this);
    }

}
