package net.neogamesmc.core.command;

import com.google.inject.Inject;
import net.md_5.bungee.api.ChatColor;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.database.operation.RawFetchOperation;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.text.Message;
import org.bukkit.entity.Player;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/10/2017 (5:35 AM)
 */
public class AccountCountCommand
{

    /**
     * Local database instance.
     */
    @Inject private Database database;

    @Command ( executor = "accounts" )
    @Permission ( Role.ADMIN )
    public void run(Player player)
    {
        try
        {
            new RawFetchOperation("SELECT COUNT(iid) FROM accounts;").task(set ->
            {
                if (set.next())
                    Message.prefix("Database").content("Currently tracking").content(set.getInt(1), ChatColor.YELLOW).content("accounts").send(player);
            }).async(database);
        }
        catch (Exception ex)
        {
            Issues.handle("Fetch Account Table Meta", ex);
        }
    }

}
