package net.neogamesmc.common.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.val;
import net.neogamesmc.common.account.Account;
import net.neogamesmc.common.config.ConfigurationProvider;
import net.neogamesmc.common.database.operation.FetchOperation;
import net.neogamesmc.common.inject.ParallelStartup;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Handle distributed player data.
 * <p>
 * This exposes both a raw data access interface
 * and access to the cache-level data.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (9:26 PM)
 */
@Singleton
@ParallelStartup
public class Database
{

    /**
     * SQL query to retrieve player data.
     * <p>
     * The {@code WHERE} clause is appended later on.
     */
    private static final String SQL_FIND_PLAYER_BASE = "SELECT accounts.iid,accounts.name,accounts.uuid,accounts.role,accounts.coins,accounts.last_login,accounts.first_login,accounts.address,settings.lobby_flight,settings.private_messages FROM accounts INNER JOIN settings ON accounts.iid=settings.account_id WHERE ";

    /**
     * Properly pool database connections.
     */
    private HikariDataSource hikari;

    /**
     * Run database operations async.
     */
    private ListeningExecutorService executor;

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
    public Database init(Logger logger, ConfigurationProvider provider)
    {
        val config = provider.read("database/standard", DatabaseConfig.class);
        val hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl(DatabaseConfig.FORMAT_JDBC_URL.apply(config));

        hikariConfig.setUsername(config.auth.username);
        hikariConfig.setPassword(config.auth.password);

        hikariConfig.setMaximumPoolSize(4);


        hikari = new HikariDataSource(hikariConfig);
        executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        cache = CacheBuilder.newBuilder().build();

        logger.info("[Database] JDBC URL: " + hikariConfig.getJdbcUrl());
        logger.info("[Database] Opened connection to MySQL instance");
        return this;
    }

    /**
     * Close out the resources associated with
     * this Database instance.
     */
    public void release()
    {
        this.executor.shutdown();
        this.hikari.close();
    }

    /**
     * Grabs a connection from our pool.
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
     * @return A {@link ListenableFuture} wrapping the return value
     */
    public <R> ListenableFuture<R> submitTask(Callable<R> task)
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
        return cache.asMap().values().stream().filter(acc -> acc.name().equals(name)).findFirst().orElse(null);
    }

    /**
     * Inserts an account into our cache.
     *
     * @param account The account
     * @return The just inserted account
     */
    public Account cacheCommit(Account account)
    {
        cache.put(account.uuid(), account);
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

    /**
     * Grab an account from our database(/cache) via a username.
     *
     * @param username Name of the player we're looking for
     * @return The account we're requesting wrapped in an {@link Optional}.
     */
    public ListenableFuture<Optional<Account>> fetchAccount(String username)
    {
        return fetchAccountInternal(null, username, true, true);
    }

    /**
     * Grab an account from our database(/cache) via a username.
     *
     * @param uuid UUID of the player we're looking for
     * @return The account we're requesting wrapped in an {@link Optional}
     */
    public ListenableFuture<Optional<Account>> fetchAccount(UUID uuid)
    {
        return fetchAccountInternal(uuid, null, true, true);
    }

    /**
     * Grab an account from our database synchronously.
     * <p>
     * Probably only ever going to be used when someone is
     * logging into a server.
     *
     * @param uuid the UUID of the player
     * @return The account we've requested
     *         wrapped in an {@link Optional}
     */
    public Optional<Account> fetchAccountSync(UUID uuid)
    {
        return fetchAccountInternal(uuid, null, true, false);
    }

    /**
     * Locally used method to fetch player data.
     *
     * @param uuid The player's UUID if required
     * @param name If we're requesting via a name, the player's username
     * @param useCache Whether or not we allow cache hits
     * @param async Whether or not to execute this request asynchronously
     * @param <R> Type-parameter for our return value
     * @return Whatever it is that we requested
     */
    @SuppressWarnings ( "unchecked" )
    private <R> R fetchAccountInternal(UUID uuid, String name, boolean useCache, boolean async)
    {
        try
        {
            val useName = uuid == null;

            final Callable<Optional<Account>> transaction = () ->
            {
                // Respect request to use local caching
                if (useCache)
                {
                    Account hit;

                    if (useName)
                        hit = cacheFetch(name);
                    else
                        hit = cacheFetch(uuid);

                    if (hit != null)
                        return Optional.of(hit);
                }


                val fetch = new FetchOperation<Account>(SQL_FIND_PLAYER_BASE + (useName ? "name=?" : "uuid=?") + " LIMIT 1;")
                                        .data(useName ? name : uuid)
                                        .type(Account.class)
                                        .sync(this);

                return fetch == null ? Optional.empty()
                                     : Optional.of(fetch);
            };


            if (async)
                return (R) executor.submit(transaction);
            else
                return (R) Futures.immediateFuture(transaction.call()).get();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

}
