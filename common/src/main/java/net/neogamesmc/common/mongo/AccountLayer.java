package net.neogamesmc.common.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.mongo.entities.Account;
import net.neogamesmc.common.text.Text;

import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/21/2017 (1:08 PM)
 */
public class AccountLayer
{

    /**
     * Local copy of our database instance.
     */
    @Inject private Database database;

    /**
     *
     */
    private Cache<UUID, Account> cache = CacheBuilder.newBuilder().build();

    private void fetchAccountInternal(UUID uuid, String name, boolean async)
    {
        val useName = uuid == null && name != null;

        val query = database.queryFor(Account.class);

        query.field(useName ? "name_lower" : "uuid")
             .equal(useName ? name : Text.stripUUID(uuid));

    }

}
