package com.neomccreations.common.database.operation;

import com.neomccreations.common.database.Database;

import java.util.concurrent.Future;

/**
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (10:09 PM)
 */
public class InsertOperation<R> implements Operation<R>
{

    // sql
    private final String sql;

    public InsertOperation(String sql)
    {
        this.sql = sql;
    }

    // data
    @Override
    public R call() throws Exception
    {
        return null;
    }

    @Override
    public R sync(Database database) throws Exception
    {
        return null;
    }

    @Override
    public Future<R> async(Database database) throws Exception
    {
        return null;
    }

}
