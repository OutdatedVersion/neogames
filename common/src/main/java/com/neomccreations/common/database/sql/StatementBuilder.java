package com.neomccreations.common.database.sql;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * @author Ben (OutdatedVersion)
 * @since May/29/2017 (9:21 PM)
 */
public class StatementBuilder
{

    /**
     *
     */
    private static Cache<Class, CachedClass> cache = CacheBuilder.newBuilder()
                                .weakKeys()
                                .expireAfterAccess(2, TimeUnit.HOURS)
                                .build();


    private final Class clazz;

    private StatementBuilder(Class clazz)
    {
        this.clazz = clazz;
    }

    public String build()
    {
        // return sql
        // INSERT INTO accounts (name) VALUES (outdatedversion);
    }

    /**
     *
     */
    public static class CachedClass
    {
        String table;

        ValidField[] fields;

        // fields we can use
        // table
        String result;
    }

    public static class ValidField
    {
        String columnName;

        Field field;
    }

}
