package net.neogamesmc.lobby;

import com.google.inject.Inject;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.event.UpdatePlayerRoleEvent;
import net.neogamesmc.core.scoreboard.PlayerSidebar;
import net.neogamesmc.core.scoreboard.PlayerSidebarManager;
import net.neogamesmc.core.scoreboard.StaticTitle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import static net.md_5.bungee.api.ChatColor.*;
import static net.neogamesmc.core.text.Colors.bold;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/04/2017 (1:37 AM)
 */
public class LobbyScoreboard
{

    static
    {
        PlayerSidebar.title(new StaticTitle(bold(GOLD) + "Neo" + bold(YELLOW) + "Games"));
    }

    /**
     * Shared database instance.
     */
    @Inject private static Database database;

    /**
     * Shared manager instance.
     */
    @Inject private static PlayerSidebarManager manager;

    /**
     * Setup a player on our scoreboard.
     *
     * @param player The player
     * @return The created sidebar
     */
    public static PlayerSidebar create(Player player)
    {
        return manager.add(player, new PlayerSidebar()
                                    .set(1, GOLD + "play.neogamesmc.net")
                                    .set(2, " ")
                                    .set(3, "0")
                                    .set(4, bold(GREEN) + "Players")
                                    .set(5, "  ")
                                    .set(6, "100")
                                    .set(7, bold(YELLOW) + "Coins")
                                    .set(8, "   ")
                                    .set(9, roleFor(player))
                                    .set(10, bold(BLUE) + "Role"))
                                    .set(11, "     ");
    }

    /**
     * Stop tracking a sidebar.
     *
     * @param player The player
     */
    public static void destroy(Player player)
    {
        manager.remove(player);
    }

    @EventHandler
    public void updateScoreboardText(UpdatePlayerRoleEvent event)
    {
        manager.sidebar(event.player).replace(9, roleFor(event.player));
    }

    /**
     * Return a formatted role for the specified player.
     *
     * @param player The player
     * @return The formatted text
     */
    private static String roleFor(Player player)
    {
        return Text.fromEnum(database.cacheFetch(player.getUniqueId()).role());
    }

}
