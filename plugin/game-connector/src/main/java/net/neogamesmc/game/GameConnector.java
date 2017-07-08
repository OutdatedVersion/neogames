package net.neogamesmc.game;

import com.google.inject.Injector;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.display.PlayerList;
import net.neogamesmc.core.scheduler.Scheduler;
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

    private Database database;

    @Override
    public void enable(Injector injector)
    {
        this.database = get(Database.class);

        loadCore();
        setupCommands();
        registerAsListener();

        unregister(PlayerList.class);

        Scheduler.timer(new CapacityWatchdog(), 40);
    }

    @Override
    public void disable()
    {
        database.release();
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
