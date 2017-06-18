package net.neogamesmc.core.chat;

import com.google.inject.Inject;
import net.neogamesmc.common.account.Account;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author Ben (OutdatedVersion)
 * @since Jun/18/2017 (3:34 AM)
 */
@ParallelStartup
public class Chat implements Listener
{

    /**
     * Local copy of our database.
     */
    @Inject private Database database;

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent event)
    {
        final Account account = database.cacheFetch(event.getPlayer().getUniqueId());
        final String name = event.getPlayer().getName(),
                  message = event.getMessage();


        event.setCancelled(true);

        System.out.printf("[Chat] %s %s: %s\n", account.role.name(), name, message);
    }

}
