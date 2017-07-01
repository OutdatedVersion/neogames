package net.neogamesmc.common.database.operation;

import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.api.Operation;
import net.neogamesmc.common.database.result.SQLConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (10:09 PM)
 */
public class InsertUpdateOperation extends Operation<Void>
{

    /**
     * After executing this operation, have the ability to grab this.
     */
    private Supplier<?> supplier;

    /**
     * Take in generated keys {@link ResultSet}.
     */
    private SQLConsumer consumer;

    /**
     * {@inheritDoc}
     */
    public InsertUpdateOperation(String sql)
    {
        super(sql);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InsertUpdateOperation data(Object... data)
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
    public <T> InsertUpdateOperation object(Supplier<T> supplier)
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
     * Update the backing consumer for this.
     *
     * @param consumer The consumer
     * @return This operation, for chaining
     */
    public InsertUpdateOperation keys(SQLConsumer consumer)
    {
        this.consumer = consumer;
        return this;
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

            if (consumer != null)
                consumer.accept(statement.getGeneratedKeys());
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
