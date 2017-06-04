package com.neomccreations.common.database.operation;

import com.neomccreations.common.database.Database;
import com.neomccreations.common.database.api.Operation;
import com.neomccreations.common.database.result.SQLResult;

import java.util.concurrent.Future;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/03/2017 (10:45 PM)
 */
public class BatchFetchOperation implements Operation<SQLResult>
{

    @Override
    public SQLResult sync(Database database) throws Exception
    {
        return null;
    }

    @Override
    public Future<SQLResult> async(Database database) throws Exception
    {
        return null;
    }

    @Override
    public SQLResult call() throws Exception
    {
        return null;
    }

}
