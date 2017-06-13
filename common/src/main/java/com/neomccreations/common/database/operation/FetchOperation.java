package com.neomccreations.common.database.operation;

import com.neomccreations.common.database.Database;
import com.neomccreations.common.database.api.Operation;
import com.neomccreations.common.database.result.SQLResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A continence tool for working with
 * SQL data-fetching queries; Mostly
 * the {@code SELECT} command.
 * <p>
 * Typical backing cycle is as follows:
 * <p>
 * 1. Query setup with object creation
 * 2. Data for the statement stored via {@link #data(Object...)}
 * 3. Statement execution in a fashion desired by {@link #sync(Database)} or {@link #async(Database)}
 *
 * @author Ben (OutdatedVersion)
 * @since May/20/2017 (1:50 AM)
 */
public class FetchOperation extends Operation<SQLResult>
{

    /**
     * Let's us know that we don't require
     * any value on {@link #data}.
     */
    private boolean requireNoData;

    /**
     * {@inheritDoc}
     */
    public FetchOperation(String sql)
    {
        super(sql);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public FetchOperation data(Object... data)
    {
        this.database = database;
        return this;
    }

    /**
     * Execute the query and transform it.
     *
     * @return Representation of the {@link ResultSet}
     * @throws Exception In the event that something goes wrong
     */
    @Override
    public SQLResult call() throws Exception
    {
        stateCheck();

        if (!requireNoData)
            checkNotNull(this.data, "You must setup the backing data first");

        try
        (
            Connection connection = this.database.reserve();
            PreparedStatement statement = OperationTools.statement(connection, this.sql, this.data);
            ResultSet result = statement.executeQuery()
        )
        {
            // probs gonna prematurely close
            return new SQLResult(result, this.database);
        }
    }

    @Override
    public SQLResult sync(Database database) throws Exception
    {
        this.database = database;
        return call();
    }

    @Override
    public Future<SQLResult> async(Database database) throws Exception
    {
        this.database = database;
        return database.submitTask(this);
    }

}
