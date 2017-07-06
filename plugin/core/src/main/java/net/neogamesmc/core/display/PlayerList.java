package net.neogamesmc.core.display;

import com.google.inject.Inject;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.inject.ParallelStartup;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.event.UpdatePlayerRoleEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.function.Function;

import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.neogamesmc.common.reference.Role.DEFAULT;

/**
 * Adds formatting to the player-list.
 *
 * @author Ben (OutdatedVersion)
 * @since Jun/19/2017 (2:41 AM)
 */
@ParallelStartup
public class PlayerList implements Listener
{

    /**
     * The custom header/footer for our player list.
     */
    private static final BaseComponent[] TOP_LINE = new ComponentBuilder("NeoGames Network").color(ChatColor.GOLD).bold(true).create(),
                                      BOTTOM_LINE = new ComponentBuilder("Visit our community at ").append("neogamesmc.net").color(ChatColor.YELLOW).create();

    /**
     * How to display the player's role in our list.
     */
    private static final Function<Role, String> LIST_FORMAT = role -> role.color + "" + ChatColor.BOLD + role.name.toUpperCase() + " ";

    /**
     * Local database instance.
     */
    @Inject private Database database;

    @EventHandler
    public void apply(PlayerLoginEvent event)
    {
        val player = event.getPlayer();

        playerList(player, database.cacheFetch(player.getUniqueId()).role());
        // player.setPlayerListHeaderFooter(TOP_LINE, BOTTOM_LINE);
    }

    /**
     * When a player's role is updated somewhere, update it instantly.
     *
     * @param event The event
     */
    @EventHandler
    public void instantUpdate(UpdatePlayerRoleEvent event)
    {
        playerList(event.player, event.fresh);
    }

    /**
     * Update how a player is displayed in the player list.
     *
     * @param player The player
     * @param role The role
     */
    private void playerList(Player player, Role role)
    {
        player.setPlayerListName((role == DEFAULT ? DEFAULT.color.toString() : LIST_FORMAT.apply(role)) + GREEN + player.getName());
    }

}
