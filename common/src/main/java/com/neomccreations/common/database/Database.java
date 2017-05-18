package com.neomccreations.common.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.neomccreations.common.account.Account;
import com.neomccreations.common.config.ConfigurationProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

}
