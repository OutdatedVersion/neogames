package net.neogamesmc.lobby;

import com.google.inject.Inject;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.event.UpdatePlayerRoleEvent;
import net.neogamesmc.core.scoreboard.PlayerSidebar;
import net.neogamesmc.core.scoreboard.PlayerSidebarManager;
import net.neogamesmc.core.scoreboard.mod.RoleTagModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.md_5.bungee.api.ChatColor.*;
import static net.neogamesmc.core.text.Colors.bold;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/04/2017 (1:37 AM)
 */
public class LobbyScoreboard implements Listener
{

    /**
     * Shared database instance.
     */
    @Inject private Database database;

    /**
     * Shared manager instance.
     */
    private PlayerSidebarManager manager;

    @Inject
    public void init(Plugin plugin, PlayerSidebarManager manager)
    {
        System.out.println("LobbyScoreboard#init");
        this.manager = manager.addDefaultModifier(plugin.get(RoleTagModifier.class));
    }

    /**
     * Setup a player on our scoreboard.
     *
     * @param player The player
     * @return The created sidebar
     */
    public PlayerSidebar create(Player player)
    {
        return new PlayerSidebar()
                            .blank()
                            .add(bold(BLUE) + "Role")
                            .add(roleFor(player))
                            .blank()
                            .add(bold(YELLOW) + "Coins")
                            .add("100")
                            .blank()
                            .add(bold(GREEN) + "Players")
                            .add("0")
                            .blank()
                            .add(GOLD + "play.neogamesmc.net")
                            .draw().registerWith(manager, player);
    }

    /**
     * Stop tracking a sidebar.
     *
     * @param player The player
     */
    public void destroy(Player player)
    {
        manager.remove(player);
    }

    @EventHandler
    public void updateScoreboardText(UpdatePlayerRoleEvent event)
    {
        // todo
    }

    /**
     * Return a formatted role for the specified player.
     *
     * @param player The player
     * @return The formatted text
     */
    private String roleFor(Player player)
    {
        return Text.fromEnum(database.cacheFetch(player.getUniqueId()).role());
    }

}
