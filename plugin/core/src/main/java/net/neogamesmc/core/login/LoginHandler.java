package net.neogamesmc.core.login;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.login.LoginHook;
import net.neogamesmc.common.mongo.AccountLayer;
import net.neogamesmc.common.mongo.Database;
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
     * Bridge to player data.
     */
    @Inject private AccountLayer source;

    /**
     * Interface to our database.
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
            val fetch = source.fetchAccountSync(event.getUniqueId());

            if (fetch.isPresent())
            {
                // TODO(Ben): replace with some sort of "conditional chain" tool
                boolean persist = false;

                val account = fetch.get();
                val addr = event.getAddress().getHostAddress();

                // Track name changes
                if (!account.name().equals(event.getName()))
                {
                    account.updateName(event.getName());
                    persist = true;
                }

                // Track IP address changes
                if (!account.addressCurrent().equals(addr))
                {
                    account.updateAddress(addr);
                    persist = true;
                }

                if (persist)
                    database.persist(account);

                // Hold account in-memory
                source.cacheInsert(account);
            }
            else
            {
                // TODO(Ben): create acct
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
        source.cacheInvalidate(event.getPlayer().getUniqueId());
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
