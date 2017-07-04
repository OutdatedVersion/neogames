package net.neogamesmc.core.scoreboard;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/04/2017 (1:15 AM)
 */
@Singleton
public class PlayerSidebarManager
{

    /**
     * Hold of every sidebar currently tracked.
     */
    private Map<UUID, PlayerSidebar> sidebars = Maps.newHashMap();

    /**
     * Start tracking a player's sidebar.
     *
     * @param player The player
     * @param bar The bar
     * @return The fresh sidebar
     */
    public PlayerSidebar add(Player player, PlayerSidebar bar)
    {
        sidebars.put(player.getUniqueId(), bar);

        if (!player.getScoreboard().equals(bar.scoreboard()))
            player.setScoreboard(bar.scoreboard());

        return bar;
    }

    /**
     * Stop tracking a player's sidebar.
     *
     * @param player The player
     * @return This manager
     */
    public PlayerSidebarManager remove(Player player)
    {
        sidebars.remove(player.getUniqueId());
        return this;
    }

    /**
     * Grab a scoreboard instance.
     *
     * @param player The player
     * @return The sidebar
     */
    public PlayerSidebar sidebar(Player player)
    {
        return sidebars.get(player.getUniqueId());
    }

}
