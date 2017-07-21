package net.neogamesmc.common.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import net.neogamesmc.common.mongo.entities.Account;

import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/21/2017 (1:08 PM)
 */
public class AccountLayer
{

    /**
     *
     */
    @Inject private Database database;

    /**
     *
     */
    private Cache<UUID, Account> cache = CacheBuilder.newBuilder().build();

}
