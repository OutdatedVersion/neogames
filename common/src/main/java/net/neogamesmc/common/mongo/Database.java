package net.neogamesmc.common.mongo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.val;
import net.neogamesmc.common.config.ConfigurationProvider;
import net.neogamesmc.common.exception.SentryHook;
import net.neogamesmc.common.mongo.converters.UUIDConverter;
import net.neogamesmc.common.mongo.entities.Account;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (12:53 AM)
 */
public class Database implements AutoCloseable
{

    // The previous system used Futures -- Sorry but I'm not a fan of them, back to a callback approach.

    /**
     * Name of the Mongo database where all of our data in-question is stored.
     */
    private static final String DATABASE_NAME = "neogames";

    /**
     * Exposes a way to run tasks asynchronous to the primary server thread.
     */
    private ExecutorService service;

    /**
     * Morphia representation of a Mongo database.
     */
    private Datastore datastore;

    @Inject
    public Database(ConfigurationProvider provider)
    {
        val morphia = new Morphia();
        val converters = morphia.getMapper().getConverters();

        // We save UUIDs in an alternative fashion (the "Mojagian" way)
        converters.addConverter(new UUIDConverter());

        // Ingest the entities for Morphia to use
        morphia.mapPackage(Account.class.getPackage().getName());


        // Load up configuration details for our database
        val config = provider.read("database/standard", DatabaseConfig.class);

        val client = new MongoClient(new ServerAddress(), Collections.singletonList(
                MongoCredential.createCredential(config.name, DATABASE_NAME, config.password.toCharArray())
        ));

        datastore = morphia.createDatastore(client, DATABASE_NAME);
        datastore.ensureIndexes(true);


        service = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                                                        .setNameFormat("database-processor-%d")
                                                        .setUncaughtExceptionHandler((thread, ex) -> SentryHook.report(ex))
                                                        .build());
    }

    @Override
    public void close() throws Exception
    {
        datastore.getMongo().close();
        service.shutdown();
    }

    /**
     * Execute the provided {@link Query} in an asynchronous fashion.
     *
     * @param query The query
     * @param callback Task to run with the results when finished
     * @param <T> Type-parameter for the query
     */
    public <T> void execute(Query<T> query, Consumer<List<T>> callback)
    {
        service.execute(() -> callback.accept(query.asList()));
    }

    /**
     * Create a query with the provided type.
     *
     * @param clazz Morphia entity representing the desired type
     * @param <T> Type of the class
     * @return A {@link Query} with the required type
     */
    public <T> Query<T> create(Class<T> clazz)
    {
        return datastore.createQuery(clazz);
    }

    /**
     * Persist the changes made in the provided {@link Entity}.
     * <p>
     * This method is non-blocking.
     *
     * @param entity The entity
     * @param callback Task to run with the result when the operation has completed
     * @param <T> Type of that entity
     */
    public <T> void save(T entity, Consumer<Key<T>> callback)
    {
        service.submit(() -> callback.accept(datastore.save(entity)));
    }

}
