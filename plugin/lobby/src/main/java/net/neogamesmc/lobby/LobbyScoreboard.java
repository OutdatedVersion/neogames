package net.neogamesmc.lobby;

import com.google.inject.Inject;
import lombok.val;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.common.text.Text;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.event.UpdatePlayerRoleEvent;
import net.neogamesmc.core.player.Players;
import net.neogamesmc.core.scheduler.Scheduler;
import net.neogamesmc.core.scoreboard.PlayerSidebar;
import net.neogamesmc.core.scoreboard.PlayerSidebarManager;
import net.neogamesmc.core.scoreboard.mod.RoleTagModifier;
import net.neogamesmc.core.scoreboard.title.StaticTitle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;

import static net.md_5.bungee.api.ChatColor.*;
import static net.neogamesmc.core.message.Colors.bold;

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
     * Local copy of our Redis instance.
     */
    @Inject private RedisHandler redis;

    /**
     * Shared manager instance.
     */
    private PlayerSidebarManager manager;

    /**
     * The total amount of players online.
     */
    private volatile long playerCount;

    @Inject
    public void init(Plugin plugin, PlayerSidebarManager manager)
    {
        this.manager = manager.addDefaultModifier(plugin.get(RoleTagModifier.class))
                              .title(new StaticTitle(bold(YELLOW) + "Neo" + bold(GOLD) + "Games"));

        Scheduler.timer(() ->
        {
            Scheduler.async(() ->
            {
                try (Jedis jedis = redis.client())
                {
                    playerCount = Long.parseLong(jedis.get("network:player_count"));
                }
            });
        }, 60);
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
                            .add(coinsFor(player))
                            .blank()
                            .add(bold(GREEN) + "Players")
                            .add(String.valueOf(playerCount))
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
        val sidebar = manager.sidebar(event.player);

        // sidebar.remove(10);
        // sidebar.set(9, roleFor(event.player));

        sidebar.modifier(RoleTagModifier.class).ifPresent(mod -> Players.stream().forEach(player -> mod.playerRefresh(player, sidebar.scoreboard())));
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

    /**
     * Returns formatted currency for a player.
     *
     * @param player The player
     * @return The formatted text
     */
    private String coinsFor(Player player)
    {
        return Text.fromCurrency(database.cacheFetch(player.getUniqueId()).coins());
    }

}
