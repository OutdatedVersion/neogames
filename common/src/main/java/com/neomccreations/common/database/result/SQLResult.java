package com.neomccreations.common.database.result;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.neomccreations.common.database.Database;
import com.neomccreations.common.database.mutate.Mutators;
import com.neomccreations.common.database.operation.InsertOperation;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (6:29 PM)
 */
public class SQLResult
{

    /**
     * Stores the {@link Field}s in a {@link Class}. From reading I don't
     * believe this sort of reflection is cached by the JVM - so we'll do it.
     *
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
                                        return Stream.of(key.getFields()).filter(ResultUtil::canUseField).toArray(Field[]::new);
                                    }
                                });

    /**
     * The SQL result backing this.
     */
    private final ResultSet result;

    /**
     * An instance of our database.
     */
    private final Database database;

    /**
     * In the event that our {@link #result} doesn't
     * contain anything we'll execute this alternate
     * operation then use it's return value as our own.
     */
    private Supplier<InsertOperation> fallback;

    /**
     * @param set See {@link #result}
     * @param database See {@link #database}
     */
    public SQLResult(ResultSet set, Database database)
    {
        this.result = set;
        this.database = database;
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
    public SQLResult orElseInsert(Supplier<InsertOperation> supplier)
    {
        this.fallback = supplier;
        return this;
    }

    /**
     * Mutates our raw results into an
     * item of the provided type.
     *
     * @param clazz The type we're looking for
     * @param <T> Type parameter for that class
     * @return The fresh item
     */
    public <T> T as(Class<T> clazz)
    {
        try
        {
            final T _instance = clazz.newInstance();
            boolean _first = true;

            for (Field field : FIELD_CACHE.get(clazz))
            {
                if (result.next())
                {
                    field.set(_instance, Mutators.of(field.getType()).from(ResultUtil.columnNameFromField(field), result));
                    _first = false;
                }
                else if (_first)
                {
                    // idk
                    if (fallback != null)
                        fallback.get().sync(this.database);
                }
                else throw new RuntimeException("Mismatched result/field count.");
            }

            return _instance;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        throw new RuntimeException("Something went wrong whilst processing that request.");
    }

}
