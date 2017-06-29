package net.neogamesmc.core.login;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.neogamesmc.common.account.Account;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.operation.FetchOperation;
import net.neogamesmc.common.database.operation.InsertOperation;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.login.LoginHook;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.issue.Issues;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;
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
     * SQL query to retrieve player data.
     */
    public static final String SQL_FIND_PLAYER = "SELECT iid,uuid,name,role,first_login,last_login,address FROM accounts WHERE uuid=?;";

    /**
     * SQL statement to insert player data.
     */
    public static final String SQL_RECORD_PLAYER = "INSERT INTO accounts (uuid, name, role, address, first_login, last_login) VALUES(?, ?, ?, ?, ?, ?);";

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
            final Account account = new FetchOperation<Account>(SQL_FIND_PLAYER)
                                            .type(Account.class)
                                            .data(event.getUniqueId())
                                            .orElseInsert(() -> new InsertOperation(SQL_RECORD_PLAYER)
                                                                      .data(event.getUniqueId(), event.getName(), Role.DEFAULT,
                                                                            event.getAddress().getHostAddress(), Instant.now(), Instant.now())
                                                                      .object(() -> new Account().fromLogin(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress())))
                                            .sync(database);

            if (account == null)
                deny(event);
            else
                database.cacheCommit(account);

            /*
            final LoginRequest request = new LoginRequest(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress());

            for (LoginHook hook : loginHooks)
            {
                hook.processLogin(request);
            }

            if (request.isDenied())
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, request.denyReason());
            */
        }
        catch (Exception ex)
        {
            deny(event);
            Issues.handle("Player Login", ex);
        }
    }

    /**
     * Deny the provided login due to some issue.
     *
     * @param event The event
     */
    private static void deny(AsyncPlayerPreLoginEvent event)
    {
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Issue encountered whilst processing login request.");
    }

}
