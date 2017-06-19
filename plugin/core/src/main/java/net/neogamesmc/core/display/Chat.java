package net.neogamesmc.core.display;

import com.google.inject.Inject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.reference.Role;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.md_5.bungee.api.ChatColor.*;

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
        event.setCancelled(true);

        final Role role = database.cacheFetch(event.getPlayer().getUniqueId()).role;
        final String name = event.getPlayer().getName();

        final BaseComponent[] message = new ComponentBuilder
                                            // start with the player's display role
                                            (role == Role.DEFAULT ? "" : role.name).color(role.color)
                                            // username -- gray w/o role, green if present
                                            .append(" " + name).color(role == Role.DEFAULT ? GRAY : GREEN)
                                            // the message
                                            .append(" " + event.getMessage()).color(WHITE).create();


        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
        System.out.printf("[Chat] %s %s: %s\n", role.name(), name, event.getMessage());
    }

}
