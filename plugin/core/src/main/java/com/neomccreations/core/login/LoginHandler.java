package com.neomccreations.core.login;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.neomccreations.common.account.Account;
import com.neomccreations.common.database.Database;
import com.neomccreations.common.database.operation.FetchOperation;
import com.neomccreations.common.database.operation.InsertOperation;
import com.neomccreations.common.inject.ParallelStartup;
import com.neomccreations.common.login.LoginHook;
import com.neomccreations.common.login.LoginRequest;
import com.neomccreations.core.issue.Issues;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Set;

/**
 * Processing players joining servers.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (11:24 PM)
 */
@ParallelStartup
public class LoginHandler implements Listener
{

    /**
     * SQL query to retrieve player data.
     */
    public static final String SQL_FIND_PLAYER = "SELECT iid,name,role,first_login,last_login,address FROM accounts WHERE uuid=?;";

    /**
     * SQL statement to insert player data.
     */
    public static final String SQl_RECORD_PLAYER = "INSERT INTO accounts (iid, uuid, name, role, address, first_login, last_login) VALUES(?, ?, ?, ?, ?, ?, ?);";

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
            final Account _account = new FetchOperation(SQL_FIND_PLAYER)
                                                  .data(event.getUniqueId())
                                                  .sync(database)
                                                  .orElseInsert(() -> new InsertOperation(SQl_RECORD_PLAYER))
                                                  .as(Account.class);


            final LoginRequest request = new LoginRequest(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress());

            for (LoginHook hook : loginHooks)
            {
                hook.processLogin(request);
            }

            if (request.isDenied())
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, request.denyReason());
        }
        catch (Exception ex)
        {
            Issues.handle("Player Login", ex);
        }
    }

}
