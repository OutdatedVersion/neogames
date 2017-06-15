package com.neomccreations.common.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.neomccreations.common.account.Account;
import com.neomccreations.common.config.ConfigurationProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Handle distributed player data.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (9:26 PM)
 */
public class Database
{

    /**
     * Properly pool database connections.
     */
    private HikariDataSource hikari;

    /**
     * Run database operations async.
     */
    private ExecutorService executor;

    /**
     * In-memory store for player's data.
     */
    private Cache<UUID, Account> cache;

    /**
     * Opens up connections to our database
     * and initializes local tools for working
     * with those connections.
     *
     * @param provider Read the config we need
     * @return The fresh database instance
     */
    @Inject
    public Database init(ConfigurationProvider provider)
    {
        final DatabaseConfig _config = provider.read("database/standard", DatabaseConfig.class);
        final HikariConfig _hikariConfig = new HikariConfig();

        _hikariConfig.setJdbcUrl(DatabaseConfig.FORMAT_JDBC_URL.apply(_config));
        _hikariConfig.setUsername(_config.auth.username);
        _hikariConfig.setPassword(_config.auth.password);

        hikari = new HikariDataSource(_hikariConfig);
        executor = Executors.newCachedThreadPool();
        cache = CacheBuilder.newBuilder().build();

        return this;
    }

    /**
     * Grabs a connection from our pool.
     *
     * <p>
     * Be sure you are closing these after use!!
     *
     * @return A fresh connection
     * @throws SQLException In case something goes wrong
     */
    public Connection reserve() throws SQLException
    {
        return hikari.getConnection();
    }

    /**
     * Adds the provided {@link Callable} to
     * our task execution queue.
     *
     * @param task The task
     * @param <R> Return type
     * @return A {@link Future} wrapping the return value
     */
    public <R> Future<R> submitTask(Callable<R> task)
    {
        return executor.submit(task);
    }

    /**
     * Grab a player's account from our
     * cache via their UUID.
     *
     * @param uuid Their UUID
     * @return Either the account or {@code null}
     */
    public Account cacheFetch(UUID uuid)
    {
        return cache.getIfPresent(uuid);
    }

    /**
     * Grab a player's account from the
     * cache using their username.
     * <p>
     * This approach traverses the value set
     * and looks into each for a name, so
     * as expected isn't the most performance
     * oriented approach. Proper lookup via
     * {@link UUID} is preferred.
     *
     * @see #cacheFetch(UUID) Should use if you can
     *
     * @param name The player's name
     * @return Their account or {@code null}
     */
    public Account cacheFetch(String name)
    {
        return cache.asMap().values().stream().filter(acc -> acc.name.equals(name)).findFirst().orElse(null);
    }

    /**
     * Inserts an account into our cache.
     *
     * @param account The account
     * @return The just inserted account
     */
    public Account cacheCommit(Account account)
    {
        cache.put(account.uuid, account);
        return account;
    }

    /**
     * Remove an account from the in-memory cache.
     *
     * @param uuid The account's UUID
     * @return This database instance
     */
    public Database cacheInvalidate(UUID uuid)
    {
        cache.invalidate(uuid);
        return this;
    }

}
