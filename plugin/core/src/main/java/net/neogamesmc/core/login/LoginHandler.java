package net.neogamesmc.core.login;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.common.account.Account;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.operation.InsertUpdateOperation;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.login.LoginHook;
import net.neogamesmc.core.issue.Issues;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

/**
 * Processing players joining servers.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (11:24 PM)
 */
@Singleton
@ParallelStartup
public class LoginHandler implements Listener
{

    /**
     * SQL statement to insert player data.
     */
    private static final String SQL_RECORD_PLAYER = "INSERT INTO accounts (uuid, name, address) VALUES(?, ?, ?);";

    /**
     * Bridge to player data.
     */
    @Inject private Database database;

    /**
     * Thread-safe set of hooks we may use.
     */
    private Set<LoginHook> loginHooks = Sets.newCopyOnWriteArraySet();

    /**
     * Add a hook into our set of registered hooks.
     *
     * @param hook The hook
     * @return This handler, for chaining
     */
    public LoginHandler registerLoginHooks(LoginHook hook)
    {
        loginHooks.add(hook);
        return this;
    }

    @EventHandler
    public void handle(AsyncPlayerPreLoginEvent event)
    {
        try
        {
            val account = database.fetchAccountSync(event.getUniqueId());

            if (account.isPresent())
            {
                database.cacheCommit(account.get().updateData(database, event.getName(), event.getAddress().getHostAddress()));
            }
            else
            {
                val creating = new Account().fromLogin(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress());

                new InsertUpdateOperation(SQL_RECORD_PLAYER)
                        .data(creating.uuid(), creating.name(), creating.ip())
                        .keys(result ->
                        {
                            if (result.next())
                                creating.id = result.getInt(1);
                        }).sync(database);

                database.cacheCommit(creating);
            }
        }
        catch (Exception ex)
        {
            deny(event);
            Issues.handle("Player Login", ex);
        }
    }

    /**
     * Remove players from our local cache.
     *
     * @param event The event
     */
    @EventHandler ( priority = EventPriority.HIGHEST )
    public void cleanup(PlayerQuitEvent event)
    {
        database.cacheInvalidate(event.getPlayer().getUniqueId());
    }

    /**
     * Deny the provided login due to some issue.
     *
     * @param event The event
     */
    private static void deny(AsyncPlayerPreLoginEvent event)
    {
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.YELLOW + "Issue encountered whilst processing login request.");
    }

}
