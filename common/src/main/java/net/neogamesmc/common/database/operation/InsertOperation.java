package net.neogamesmc.common.database.operation;

import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.api.Operation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (10:09 PM)
 */
public class InsertOperation extends Operation<Void>
{

    /**
     * After executing this operation, have the ability to grab this.
     */
    private Supplier<?> supplier;

    /**
     * {@inheritDoc}
     */
    public InsertOperation(String sql)
    {
        super(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InsertOperation data(Object... data)
    {
        this.data = data;
        return this;
    }

    /**
     *
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> InsertOperation object(Supplier<T> supplier)
    {
        this.supplier = supplier;
        return this;
    }

    /**
     *
     * @param <T>
     * @return
     */
    public <T> Supplier<T> object()
    {
        return (Supplier<T>) supplier;
    }


    /**
     * Sends off the SQL here.
     *
     * @return Whatever we needed
     * @throws Exception In the event that something goes wrong
     */
    @Override
    public Void call() throws Exception
    {
        stateCheck();

        try
        (
            Connection connection = database.reserve();
            PreparedStatement statement = OperationTools.statement(connection, this.sql, this.data)
        )
        {
            statement.executeUpdate();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void sync(Database database) throws Exception
    {
        this.database = database;
        return call();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Void> async(Database database) throws Exception
    {
        this.database = database;
        return database.submitTask(this);
    }

}
