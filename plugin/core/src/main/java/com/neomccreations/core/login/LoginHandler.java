package com.neomccreations.core.login;

import com.google.inject.Inject;
import com.neomccreations.common.account.Account;
import com.neomccreations.common.database.Database;
import com.neomccreations.common.database.operation.FetchOperation;
import com.neomccreations.common.database.operation.InsertOperation;
import com.neomccreations.common.inject.ParallelStartup;
import com.neomccreations.core.issue.Issues;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Processes players joining servers.
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
    public static final String SQL_FIND_PLAYER = "SELECT id,name,role,first_login,last_login,address FROM accounts WHERE uuid=?;";

    /**
     * Bridge to player data.
     */
    @Inject private Database database;

    @EventHandler
    public void handle(AsyncPlayerPreLoginEvent event)
    {
        try
        {
            final Account _account = new FetchOperation(SQL_FIND_PLAYER)
                                            .data(event.getUniqueId())
                                            .sync(database)
                                            .orElseInsert(() -> new InsertOperation(""))
                                            // INSERT INTO accounts (name) VALUES (outdatedversion);
                                            // need some sort of fallback task here
                                            .as(Account.class); // can't complete w/o err if not in db
        }
        catch (Exception ex)
        {
            Issues.handle("Player Login", ex);
        }
    }

}
