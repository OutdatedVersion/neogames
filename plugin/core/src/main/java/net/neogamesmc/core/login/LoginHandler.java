package net.neogamesmc.core.login;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import net.neogamesmc.common.account.Account;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.operation.InsertUpdateOperation;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.issue.Issues;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nullable;

import java.util.Optional;

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
     * Fetch player data from our database, and cache it. In the case that they do not have data yet, create it.
     *
     * @param event The asynchronous login event
     */
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

                new InsertUpdateOperation(SQL_RECORD_PLAYER).data(creating.uuid(), creating.name(), creating.ip()).keys(result -> {
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

    @EventHandler
    public void handleOp(PlayerJoinEvent e)
    {
        val acc = database.fetchAccount(e.getPlayer().getUniqueId()); //Get the acc
        Futures.addCallback(acc, new FutureCallback<Optional<Account>>()
        {
            @Override
            public void onSuccess(@Nullable Optional<Account> account)
            {
                account.ifPresent(a -> {
                    if (a.role() == Role.DEV || a.role() == Role.OWNER)
                    {
                        //Op em
                        e.getPlayer().setOp(true);
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable)
            {

            }
        });
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
