package net.neogamesmc.lobby;

import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.text.Message;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.RED;
import static net.md_5.bungee.api.ChatColor.YELLOW;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/08/2017 (11:56 AM)
 */
public class FlyCommand implements Listener
{

    /**
     * Locally held database instance.
     */
    @Inject private Database database;

    /**
     * Expose a method for the player to toggle this setting.
     *
     * @param player The player
     */
    @Command ( executor = "fly" )
    @Permission ( value = Role.DUDE, note = "Sorry, this is currently only for donators!" )
    public void run(Player player)
    {
        if (player.getGameMode() == GameMode.CREATIVE)
        {
            Message.prefix("Lobby").content("May want to get out of creative mode before using this. ;)", YELLOW).send(player);
            return;
        }

        // Change database side
        val nowUsing = toggle(player);

        // Toggle Bukkit state
        player.setAllowFlight(nowUsing);
        player.setFlying(nowUsing);

        // Inform
        Message.prefix("Lobby").content("Flight is now " + (nowUsing ? "enabled" : "disabled"), nowUsing ? GREEN : RED).send(player);
    }

    /**
     * Enable/disable flying for a player based on the setting when they join.
     *
     * @param event The event
     */
    @EventHandler ( priority = EventPriority.LOW )
    public void handleLogin(PlayerJoinEvent event)
    {
        val player = event.getPlayer();
        val enabled = enabled(player);

        player.setAllowFlight(enabled);
        player.setFlying(enabled);
    }

    /**
     * Check whether or not the provided player has flight enabled.
     *
     * @param player The player
     * @return Yes or no
     */
    private boolean enabled(Player player)
    {
        return database.cacheFetch(player.getUniqueId()).lobbyFlight();
    }

    /**
     * Toggle the state of a player's lobby flight setting.
     *
     * @param player The player
     * @return What the new state is
     */
    private boolean toggle(Player player)
    {
        val account = database.cacheFetch(player.getUniqueId());

        // Invert
        account.lobbyFlight(!account.lobbyFlight(), database);

        return account.lobbyFlight();
    }

}
