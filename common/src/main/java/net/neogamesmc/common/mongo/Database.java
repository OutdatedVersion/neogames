package net.neogamesmc.common.mongo;

import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.val;
import net.neogamesmc.common.config.ConfigurationProvider;
import net.neogamesmc.common.mongo.converters.UUIDConverter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.Collections;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (12:53 AM)
 */
public class Database implements AutoCloseable
{

    /**
     * Name of the Mongo database where all of our data in-question is stored.
     */
    private static final String DATABASE_NAME = "neogames";

    /**
     *
     */
    private Datastore datastore;

    @Inject
    public Database(ConfigurationProvider provider)
    {
        val morphia = new Morphia();
        val converters = morphia.getMapper().getConverters();

        // We save UUIDs specially
        converters.addConverter(new UUIDConverter());

        // Ingest the entities for Morphia to use
        morphia.mapPackage("net.neogamesmc.common.mongo.entities");

        // Load up configuration details for our database
        val config = provider.read("database/standard", DatabaseConfig.class);

        val client = new MongoClient(new ServerAddress(), Collections.singletonList(
                MongoCredential.createCredential(config.name, DATABASE_NAME, config.password.toCharArray())
        ));

        datastore = morphia.createDatastore(client, DATABASE_NAME);
        datastore.ensureIndexes();
    }

    @Override
    public void close() throws Exception
    {
        datastore.getMongo().close();
    }

}
