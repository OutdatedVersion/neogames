package net.neogamesmc.common.database.operation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.api.Operation;
import net.neogamesmc.common.database.mutate.Mutators;
import net.neogamesmc.common.database.result.ResultTools;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
public class FetchOperation<V> extends Operation<V>
{

    /**
     * Stores the {@link Field}s in a {@link Class}. From reading I don't
     * believe this sort of reflection is cached by the JVM - so we'll do it.
     * <p>
     * Also, we take into account that we may only benefit from fields with
     * a select set of annotations.
     */
    private static final LoadingCache<Class<?>, Field[]> FIELD_CACHE = CacheBuilder.newBuilder()
            .weakKeys()
            .build(new CacheLoader<Class<?>, Field[]>()
            {
                @Override
                public Field[] load(Class<?> key) throws Exception
                {
                    return Stream.of(key.getFields()).filter(ResultTools::canUseField).toArray(Field[]::new);
                }
            });

    /**
     * Let's us know that we don't require
     * any value on {@link #data}.
     */
    private boolean requireNoData = true;

    /**
     * In the event that our {@link ResultSet} doesn't
     * contain anything we'll execute this alternate
     * operation then use it's return value as our own.
     */
    private Supplier<InsertUpdateOperation> fallback;

    /**
     * Type of our type-parameter.
     */
    private Class<V> clazz;

    /**
     * {@inheritDoc}
     */
    public FetchOperation(String sql)
    {
        super(sql);
    }

    /**
     * In the event that we weren't able
     * to fetch any sort of data from
     * the query we'll run this code instead
     * of attempting a conversion.
     *
     * @param supplier The code to execute
     * @return This result for chaining
     *
     * @see #fallback Some more info
     */
    public FetchOperation<V> orElseInsert(Supplier<InsertUpdateOperation> supplier)
    {
        this.fallback = supplier;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FetchOperation<V> data(Object... data)
    {
        this.requireNoData = false;
        this.data = data;
        return this;
    }

    /**
     * Updates the underlying class for this operation.
     * <p>
     * It should match the type-parameter.
     *
     * @param clazz The class
     * @return This operation; for chaining
     */
    public FetchOperation<V> type(Class<V> clazz)
    {
        this.clazz = clazz;
        return this;
    }

    /**
     * Execute the query and transform it.
     *
     * @return Representation of the {@link ResultSet}
     * @throws Exception In the event that something goes wrong
     */
    @Override
    @SuppressWarnings ( "unchecked" )
    public V call() throws Exception
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
            final V instance = clazz.newInstance();

            if (result.next())
            {
                for (Field field : FIELD_CACHE.get(clazz))
                {
                    field.set(instance, Mutators.of(field.getType()).from(ResultTools.columnNameFromField(field), result));
                }

                return instance;
            }
            else
            {
                checkNotNull(fallback, "Missing required operation fallback");
                fallback.get().sync(this.database);
                return (V) fallback.get().object().get();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        throw new RuntimeException("Unknown issue occurred whilst processing request.");
    }

    @Override
    public V sync(Database database) throws Exception
    {
        this.database = database;
        return call();
    }

    @Override
    public Future<V> async(Database database) throws Exception
    {
        this.database = database;
        return database.submitTask(this);
    }

}
