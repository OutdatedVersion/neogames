package net.neogamesmc.common.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.common.mongo.entities.Account;
import net.neogamesmc.common.text.Text;
import org.mongodb.morphia.query.FindOptions;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/21/2017 (1:08 PM)
 */
@Singleton
public class AccountLayer
{

    private static final FindOptions FIND_OPTIONS = new FindOptions().limit(1).maxTime(4, TimeUnit.SECONDS);


    /**
     * Local copy of our database instance.
     */
    @Inject private Database database;

    /**
     *
     */
    private Cache<UUID, Account> cache = CacheBuilder.newBuilder().build();

    /**
     * Take in the provided UUID and username to return a task that may be executed to find their account.
     * <p>
     * Either one of the arguments may be null, but <strong>not</strong> both.
     */
    private final BiFunction<UUID, String, Callable<Optional<Account>>> TASK_CREATOR = (uuid, name) -> () ->
    {
        val useName = uuid == null && name != null;

        return Optional.ofNullable(
                database.queryFor(Account.class)
                        .field(useName ? "name_lower" : "uuid").equal(useName ? name : Text.stripUUID(uuid))
                        .get(FIND_OPTIONS)
        );
    };

    /**
     * Retrieve an account by UUID from our database.
     * <p>
     * This is a thread-blocking method -- be weary of where you use it.
     *
     * @param uuid The UUID to look for
     * @return A wrapper {@link Account}
     * @throws Exception In the event that something goes wrong
     */
    public Optional<Account> fetchAccountSync(UUID uuid) throws Exception
    {
        return TASK_CREATOR.apply(uuid, null).call();
    }

}
