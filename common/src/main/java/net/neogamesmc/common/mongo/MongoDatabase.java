package net.neogamesmc.common.mongo;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import lombok.val;
import net.neogamesmc.common.config.ConfigurationProvider;
import net.neogamesmc.common.mongo.converters.UUIDConverter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/19/2017 (12:53 AM)
 */
public class MongoDatabase implements AutoCloseable
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
    public MongoDatabase(ConfigurationProvider provider)
    {
        val morphia = new Morphia();
        val converters = morphia.getMapper().getConverters();

        converters.addConverter(new UUIDConverter());

        // Ingest the entities for Morphia to use
        morphia.mapPackage("net.neogamesmc.common.mongo.morphia");

        // TODO(Ben): credentials
        datastore = morphia.createDatastore(new MongoClient(new ServerAddress(), Lists.newArrayList()), DATABASE_NAME);
        datastore.ensureIndexes();
    }

    @Override
    public void close() throws Exception
    {
        datastore.getMongo().close();
    }

}
