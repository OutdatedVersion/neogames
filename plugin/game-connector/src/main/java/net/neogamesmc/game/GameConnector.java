package net.neogamesmc.game;

import com.google.inject.Injector;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.redis.RedisChannel;
import net.neogamesmc.common.redis.RedisHandler;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.display.PlayerList;
import net.neogamesmc.core.scheduler.Scheduler;
import net.neogamesmc.game.death.PreventRespawn;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.md_5.bungee.api.ChatColor.YELLOW;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/07/2017 (10:02 PM)
 */
public class GameConnector extends Plugin
{

    /**
     * Locally held database instance.
     */
    private Database database;

    @Override
    public void enable(Injector injector)
    {
        this.database = get(Database.class);
        get(RedisHandler.class).subscribe(RedisChannel.DEFAULT);

        loadCore();
        setupCommands();
        registerAsListener();

        register(InventoryHandler.class);
        register(PreventRespawn.class);

        get(PlayerList.class).mode(PlayerList.Mode.NO_ROLES);

        Scheduler.timer(new CapacityWatchdog(), CapacityWatchdog.INTERVAL);
    }

    @Override
    public void disable()
    {
        database.release();
        get(RedisHandler.class).release();
    }

    @EventHandler
    public void formatJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(baseMessage(event.getPlayer())  + " has joined");
    }

    @EventHandler
    public void formatQuit(PlayerQuitEvent event)
    {
        event.setQuitMessage(baseMessage(event.getPlayer()) + " has left");
    }

    /**
     * Grab the beginning half of the formatting messages.
     *
     * @param player The player
     * @return The beginning
     */
    private String baseMessage(Player player)
    {
        return database.cacheFetch(player.getUniqueId()).role().color + player.getName() + YELLOW;
    }

}
