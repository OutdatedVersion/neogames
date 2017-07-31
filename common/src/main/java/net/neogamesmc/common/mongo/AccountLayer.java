package net.neogamesmc.common.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import lombok.val;
import net.neogamesmc.common.mongo.entities.Account;
import net.neogamesmc.common.task.Callback;
import net.neogamesmc.common.text.Text;
import org.mongodb.morphia.query.FindOptions;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/21/2017 (1:08 PM)
 */
@Singleton
public class AccountLayer
{

    /**
     * The default options for an operation to retrieve an account from our datasource.
     */
    private static final FindOptions FIND_OPTIONS = new FindOptions().limit(1).maxTime(4, TimeUnit.SECONDS);

    /**
     * Local copy of our database instance.
     */
    @Inject private Database database;

    /**
     * Player accounts are held in-memory while they are online.
     * <p>
     * Invalidation <strong>needs to be properly handled</strong> when they disconnect.
     */
    private Cache<UUID, Account> cache = CacheBuilder.newBuilder().build();

    /**
     * Take in the provided UUID or username to return a task that may be executed to
     * find their account using either key provided.
     * <p>
     * Either one of the arguments may be null, but <strong>not both</strong>.
     */
    private final BiFunction<UUID, String, Callable<Optional<Account>>> TASK_CREATOR = (uuid, name) -> () ->
    {
        val useName = uuid == null && name != null;


        // Attempt a local cache hit before an expensive call
        if (useName)
        {
            // Iteration over cache elements to lookup by name
            // performs poorly compared to the O(1) lookup of the UUID.
            // It should be avoided as much as possible.
            for (Account account : cache.asMap().values())
            {
                if (account.name().equals(name))
                    return Optional.of(account);
            }
        }
        else
        {
            val attempt = cache.getIfPresent(uuid);

            if (attempt != null)
                return Optional.of(attempt);
        }


        // No luck there, let's look for them
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

    /**
     * Retrieve a player's account asynchronously using their name as the key.
     *
     * @param name The player's name
     * @param callback A callback to process the result
     */
    public void fetchAccount(@NonNull String name, Callback<Optional<Account>> callback)
    {
        checkArgument(name.length() <= 16, "The username provided is too long; perhaps you tried supplying a UUID in string form?");

        try
        {
            callback.success(TASK_CREATOR.apply(null, name).call());
        }
        catch (Exception ex)
        {
            callback.failure(ex);
        }
    }

    /**
     * Retrieve a player's account asynchronously using their UUID as the key.
     *
     * @param uuid The player's UUID
     * @param callback A callback to process the result
     */
    public void fetchAccount(@NonNull UUID uuid, Callback<Optional<Account>> callback)
    {
        database.execute(() ->
        {
            try
            {
                callback.success(TASK_CREATOR.apply(uuid, null).call());
            }
            catch (Exception ex)
            {
                callback.failure(ex);
            }
        });
    }

}
